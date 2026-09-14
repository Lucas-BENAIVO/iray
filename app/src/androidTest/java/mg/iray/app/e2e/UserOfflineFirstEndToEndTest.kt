package mg.iray.app.e2e

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import mg.iray.app.db.AppDatabase
import mg.iray.app.e2e.gateways.FakeAuthSource
import mg.iray.app.e2e.gateways.FakeRemoteSync
import mg.iray.app.e2e.gateways.InMemorySession
import mg.iray.app.repository.AuthRepository
import mg.iray.app.repository.UserProfileRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E du flux utilisateur offline-first (cf auth.md) sur une vraie base Room.
 */
@RunWith(AndroidJUnit4::class)
class UserOfflineFirstEndToEndTest {

    private lateinit var db: AppDatabase
    private val auth = FakeAuthSource()
    private val session = InMemorySession()
    private val remote = FakeRemoteSync()

    @After
    fun tearDown() {
        db.close()
    }

    private fun uiAuthRepo() = AuthRepository(
        auth, session,
        db.userDao(), db.userProfileDao(), db.taskDao(),
        db.signalementDao(), db.mediaDao(), db.notificationDao()
    )

    @Test
    fun premiereOuvertureIdUnique_puisReutilisation_etAjoutInfosSync() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())

        // 1. première ouverture : uid anonyme unique Firebase
        val uid = uiAuthRepo().ensureUserId()
        assertFalse(uid.startsWith("local-"))
        assertEquals(uid, session.getUid())

        // 2. "relance" de l'app : même uid rejoué (entrées futures)
        val uidRelaunch = uiAuthRepo().ensureUserId()
        assertEquals(uid, uidRelaunch)

        // 3. ajout d'informations (offline-first) : écrit dans Room avant toute sync
        val profile = uiAuthRepo().addProfileInfo(
            firstName = "Hery",
            lastName = "Rabe",
            phone = "034 12 345 67",
            email = "hery.rabe@example.mg"
        )
        assertEquals(uid, profile.uid)
        assertEquals("Hery", db.userProfileDao().get(uid)!!.firstName)
        assertEquals("UPDATE", db.userProfileDao().get(uid)!!.pendingOperation)

        // 4. retour réseau : synchronisation du profil vers users/{uid}/profile
        val profileRepo = UserProfileRepository(db.userProfileDao(), remote) { uid }
        profileRepo.pushPendingChanges()

        assertTrue(remote.contains("users/$uid/profile", uid))
        assertEquals("Hery", remote.docs["users/$uid/profile"]!![uid]!!.first["firstName"])
        assertNull(db.userProfileDao().get(uid)?.pendingOperation)
    }

    @Test
    fun premierLancementHorsLigne_creeUidLocal_puisMigreAuRetourReseau() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        auth.fail = true

        val localUid = uiAuthRepo().ensureUserId()
        assertTrue(localUid.startsWith("local-"))

        db.taskDao().upsert(
            mg.iray.app.entity.TaskEntity(id = "t1", userId = localUid, title = "Hors-ligne", pendingOperation = "UPDATE")
        )

        auth.fail = false
        val realUid = uiAuthRepo().ensureRealUid()

        assertFalse(realUid.startsWith("local-"))
        assertEquals(realUid, session.getUid())
        assertEquals(realUid, db.taskDao().get("t1")!!.userId)
        assertNull(db.userDao().get(localUid))
    }
}