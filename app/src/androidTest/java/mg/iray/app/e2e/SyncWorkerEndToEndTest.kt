package mg.iray.app.e2e

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.Operation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
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
import mg.iray.app.entity.TaskEntity
import mg.iray.app.worker.SyncWorker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E WorkManager : le worker de synchronisation pousse les changements
 * locaux vers le remote et pull les changements distants dans Room.
 */
@RunWith(AndroidJUnit4::class)
class SyncWorkerEndToEndTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val remote = FakeRemoteSync()

    @Before
    fun init() {
        ServiceLocator.db = AppDatabase.inMemory(context)
        ServiceLocator.session = InMemorySession()
        ServiceLocator.authSource = FakeAuthSource()
        ServiceLocator.remoteSync = remote
        ServiceLocator.mediaUploader = FakeMediaUploader()

        runBlocking { ServiceLocator.session.saveUid("uid1") }

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
    fun syncWorker_pousseLesChangementsLocaux_etPullLesDistants() = runBlocking {
        ServiceLocator.db.taskDao().upsert(
            TaskEntity(id = "t1", userId = "uid1", title = "Locale", pendingOperation = "UPDATE")
        )
        remote.docs["users/uid1/tasks"] = mutableMapOf(
            "t2" to (mapOf("id" to "t2", "title" to "Distante", "isDone" to true, "updatedAt" to 100L) to 100L)
        )

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        val state = WorkManager.getInstance(context).enqueue(request).result.get()

        assertTrue(state is Operation.State.SUCCESS)
        val info = WorkManager.getInstance(context)
            .getWorkInfosByTag(SyncWorker::class.java.name).get().first()
        assertEquals(WorkInfo.State.SUCCEEDED, info.state)

        assertTrue(remote.contains("users/uid1/tasks", "t1"))
        assertEquals("Locale", remote.docs["users/uid1/tasks"]!!["t1"]!!.first["title"])

        val tasks = ServiceLocator.db.taskDao().observeTasks("uid1").first()
        assertEquals(setOf("t1", "t2"), tasks.map { it.id }.toSet())
        assertEquals("Distante", tasks.first { it.id == "t2" }.title)
        assertNull(ServiceLocator.db.taskDao().get("t1")?.pendingOperation)
    }

    @Test
    fun syncWorker_startsWithCurrentUser_failure() = runBlocking {
        ServiceLocator.db.taskDao().upsert(
            TaskEntity(id = "t1", userId = "uid1", title = "Locale", pendingOperation = "UPDATE")
        )

        // sans uid enregistré, le worker ne doit pas crasher sur les données
        ServiceLocator.session.clear()
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        val state = WorkManager.getInstance(context).enqueue(request).result.get()

        assertTrue(state is Operation.State.FAILURE)
        assertEquals("UPDATE", ServiceLocator.db.taskDao().get("t1")?.pendingOperation)
    }

    @Test
    fun syncWorker_resteEnEchec_etConserveLesChangementsSansReseau() = runBlocking {
        ServiceLocator.db.taskDao().upsert(
            TaskEntity(id = "t1", userId = "uid1", title = "Locale", pendingOperation = "UPDATE")
        )
        (ServiceLocator.remoteSync as FakeRemoteSync).apply {
            failList = true
            failSet = true
        }

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)

        // exécution synchrone du worker pendant enqueue : on relit son état
        val info = WorkManager.getInstance(context)
            .getWorkInfosByTag(SyncWorker::class.java.name).get()[0]
        assertTrue(
            "le worker échoue sans réseau (pas SUCCEEDED)",
            info.state != WorkInfo.State.SUCCEEDED
        )
        assertEquals(
            "les changements locaux sont conservés hors-ligne",
            "UPDATE", ServiceLocator.db.taskDao().get("t1")?.pendingOperation
        )
        assertTrue(
            "aucune donnée ne part au remote hors-ligne",
            !remote.contains("users/uid1/tasks", "t1")
        )
    }
}