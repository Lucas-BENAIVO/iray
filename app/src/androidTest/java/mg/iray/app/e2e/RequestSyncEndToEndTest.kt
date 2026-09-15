package mg.iray.app.e2e

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.Operation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mg.iray.app.db.AppDatabase
import mg.iray.app.di.ServiceLocator
import mg.iray.app.e2e.gateways.FakeAuthSource
import mg.iray.app.e2e.gateways.FakeMediaUploader
import mg.iray.app.e2e.gateways.FakeRemoteSync
import mg.iray.app.e2e.gateways.InMemorySession
import mg.iray.app.entity.RequestEntity
import mg.iray.app.worker.SyncWorker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E de l'envoi d'une demande (request) avec synchronisation Firebase :
 * pull des référentiels (territories, procedures) -> soumission locale
 * offline-first -> le SyncWorker pousse la demande vers users/{uid}/requests.
 */
@RunWith(AndroidJUnit4::class)
class RequestSyncEndToEndTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val remote = FakeRemoteSync()
    private val uid = "uid-e2e"

    @Before
    fun init() {
        ServiceLocator.db = AppDatabase.inMemory(context)
        ServiceLocator.session = InMemorySession()
        ServiceLocator.authSource = FakeAuthSource()
        ServiceLocator.remoteSync = remote
        ServiceLocator.mediaUploader = FakeMediaUploader()

        runBlocking { ServiceLocator.session.saveUid(uid) }

        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder().setExecutor(SynchronousExecutor()).build()
        )
    }

    @After
    fun tearDown() {
        WorkManager.getInstance(context).cancelAllWork()
        ServiceLocator.db.close()
    }

    @Test
    fun envoiRequest_referencePullee_etSynchroniseeVersFirestore() = runBlocking {
        // 1. Referentiels distants (administres) : territoire + procedure
        seedTerritories()
        seedProcedure()

        // 2. SyncWorker : pull des referentiels dans Room
        runWorker()

        val territory = ServiceLocator.db.territoryDao().get("fkt_001")
        assertNotNull(territory)
        assertEquals("Andravoahangy", territory!!.name)
        assertEquals("FOKONTANY", territory.type)

        val procedure = ServiceLocator.db.procedureDao().get("residence_certificate")
        assertNotNull(procedure)
        assertEquals("Certificat de residence", procedure!!.name)

        // 3. L'utilisateur soumet une demande (offline-first : ecrit localement)
        val submitted = ServiceLocator.requestRepository().submit(
            RequestEntity(
                id = "req_001",
                userId = uid,
                procedureId = procedure.id,
                territoryId = territory.id,
                documents = listOf("certificat.pdf"),
                formData = mapOf("fullName" to "Hery Rabe", "age" to 30)
            )
        )
        assertTrue(submitted.referenceNumber.startsWith("REQ-"))
        assertEquals("UPDATE", ServiceLocator.db.requestDao().get("req_001")!!.pendingOperation)

        // 4. SyncWorker : pousse la demande vers Firestore (users/{uid}/requests)
        runWorker()

        val collection = "users/$uid/requests"
        assertTrue(remote.contains(collection, "req_001"))
        val remoteData = remote.docs[collection]!!["req_001"]!!.first
        assertEquals("residence_certificate", remoteData["procedureId"])
        assertEquals("fkt_001", remoteData["territoryId"])
        assertEquals("SUBMITTED", remoteData["status"])
        assertEquals(submitted.referenceNumber, remoteData["referenceNumber"])
        assertEquals(listOf("certificat.pdf"), remoteData["documents"])
        assertEquals(mapOf("fullName" to "Hery Rabe", "age" to 30), remoteData["formData"])

        assertNull(ServiceLocator.db.requestDao().get("req_001")!!.pendingOperation)

        // 5. Mise a jour de statut cote administration puis pull
        remote.docs[collection]!!["req_001"] = (
            remoteData.toMutableMap().apply {
                put("status", "IN_PROGRESS")
                put("updatedAt", System.currentTimeMillis())
            } to System.currentTimeMillis()
        )
        ServiceLocator.requestRepository().pullRemoteChanges()

        val local = ServiceLocator.db.requestDao().get("req_001")!!
        assertEquals("IN_PROGRESS", local.status)
        assertEquals(mapOf("fullName" to "Hery Rabe", "age" to 30), local.formData)
        assertNull(local.pendingOperation)
    }

    @Test
    fun envoiRequest_horsLigne_estConserve_puisSynchroAuRetourReseau() = runBlocking {
        seedProcedure()
        runWorker()

        // 1. Saisie hors-ligne : le remote echoue
        (ServiceLocator.remoteSync as FakeRemoteSync).failSet = true
        ServiceLocator.requestRepository().submit(
            RequestEntity(
                id = "req_offline",
                userId = uid,
                procedureId = "residence_certificate",
                territoryId = "fkt_001"
            )
        )
        runWorker()
        assertFalse(remote.contains("users/$uid/requests", "req_offline"))
        assertEquals("UPDATE", ServiceLocator.db.requestDao().get("req_offline")!!.pendingOperation)

        // 2. Retour du reseau : la demande part vers Firestore
        (ServiceLocator.remoteSync as FakeRemoteSync).failSet = false
        ServiceLocator.requestRepository().pushPendingChanges()

        assertTrue(remote.contains("users/$uid/requests", "req_offline"))
        assertEquals("SUBMITTED", remote.docs["users/$uid/requests"]!!["req_offline"]!!.first["status"])
        assertNull(ServiceLocator.db.requestDao().get("req_offline")!!.pendingOperation)
    }

    private suspend fun runWorker() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        val state = WorkManager.getInstance(context).enqueue(request).result.get()
        assertTrue(state is Operation.State.SUCCESS)
    }

    private suspend fun seedTerritories() {
        remote.docs["territories"] = mutableMapOf(
            "reg_001" to (mapOf(
                "id" to "reg_001",
                "name" to "Analamanga",
                "type" to "REGION",
                "updatedAt" to 100L
            ) to 100L),
            "fkt_001" to (mapOf(
                "id" to "fkt_001",
                "name" to "Andravoahangy",
                "type" to "FOKONTANY",
                "parentId" to "com_001",
                "updatedAt" to 100L
            ) to 100L)
        )
    }

    private suspend fun seedProcedure() {
        remote.docs["procedures"] = mutableMapOf(
            "residence_certificate" to (mapOf(
                "id" to "residence_certificate",
                "name" to "Certificat de residence",
                "description" to "Attestation de residence",
                "category" to "IDENTITY",
                "requiredDocuments" to listOf("CNI", "Acte de naissance"),
                "territoryType" to "FOKONTANY",
                "estimatedProcessingDays" to 5,
                "isFree" to true,
                "isActive" to true,
                "updatedAt" to 100L
            ) to 100L)
        )
    }
}