package mg.iray.app.sync

import kotlinx.coroutines.test.runTest
import mg.iray.app.entity.NotificationEntity
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.entity.TaskEntity
import mg.iray.app.entity.UserProfileEntity
import mg.iray.app.fakes.FakeNotificationDao
import mg.iray.app.fakes.FakeRemoteSync
import mg.iray.app.fakes.FakeSignalementDao
import mg.iray.app.fakes.FakeTaskDao
import mg.iray.app.fakes.FakeUserProfileDao
import mg.iray.app.fakes.InMemorySession
import mg.iray.app.repository.NotificationRepository
import mg.iray.app.repository.SignalementRepository
import mg.iray.app.repository.TaskRepository
import mg.iray.app.repository.UserProfileRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineFirstSyncTest {

    private fun uid() = "uid1"

    @Test
    fun `modification locale est poussee vers le remote`() = runTest {
        val dao = FakeTaskDao()
        val remote = FakeRemoteSync()
        val repo = TaskRepository(dao, remote) { uid() }

        repo.addOrUpdateTask(TaskEntity(id = "t1", title = "Acheter du riz"))

        assertTrue("l'écriture est d'abord locale, non synchronisée", !dao.get("t1")!!.isSynced)

        repo.pushPendingChanges()

        assertTrue(remote.contains("users/uid1/tasks", "t1"))
        assertTrue(dao.get("t1")!!.isSynced)
        assertNull(dao.get("t1")?.pendingOperation)
    }

    @Test
    fun `modification hors ligne reste pending et part au retour du reseau`() = runTest {
        val dao = FakeTaskDao()
        val remote = FakeRemoteSync()
        val repo = TaskRepository(dao, remote) { uid() }
        repo.addOrUpdateTask(TaskEntity(id = "t1", title = "Hors-ligne"))

        remote.failSet = true
        repo.pushPendingChanges()
        assertFalse("échec réseau : la tâche reste non synchronisée", dao.get("t1")!!.isSynced)
        assertEquals("UPDATE", dao.get("t1")?.pendingOperation)
        assertFalse(remote.contains("users/uid1/tasks", "t1"))

        remote.failSet = false
        repo.pushPendingChanges()
        assertTrue(dao.get("t1")!!.isSynced)
        assertTrue(remote.contains("users/uid1/tasks", "t1"))
    }

    @Test
    fun `le pull applique le plus recent ecrit gagne (dernier écrit gagne)`() = runTest {
        val dao = FakeTaskDao()
        val remote = FakeRemoteSync()
        val repo = TaskRepository(dao, remote) { uid() }

        // t1 : remote plus récent que le local -> le remote gagne
        dao.upsert(TaskEntity(id = "t1", userId = uid(), title = "Locale", updatedAt = 1000L, pendingOperation = "UPDATE"))
        // t2 : présent uniquement chez le remote -> créé localement
        remote.docs["users/uid1/tasks"] = mutableMapOf(
            "t1" to (mapOf("id" to "t1", "title" to "Distante", "isDone" to true, "updatedAt" to 2000L) to 2000L),
            "t2" to (mapOf("id" to "t2", "title" to "Ajout distant", "isDone" to false, "updatedAt" to 3000L) to 3000L)
        )
        // t3 : local plus récent que le remote -> on garde le local pending
        dao.upsert(TaskEntity(id = "t3", userId = uid(), title = "Locale recente", updatedAt = 4000L, pendingOperation = "UPDATE"))
        remote.docs["users/uid1/tasks"]!!["t3"] =
            mapOf("id" to "t3", "title" to "Ancien", "isDone" to true, "updatedAt" to 1000L) to 1000L

        repo.pullRemoteChanges()

        assertEquals("Distante", dao.get("t1")?.title)
        assertTrue(dao.get("t1")?.isSynced == true)
        assertNull(dao.get("t1")?.pendingOperation)

        assertEquals("Ajout distant", dao.get("t2")?.title)
        assertTrue(dao.get("t2")?.isSynced == true)

        assertEquals("Locale recente", dao.get("t3")?.title)
        assertTrue("le local le plus récent n'est pas écrasé", dao.get("t3")!!.isSynced == false)
        assertEquals("UPDATE", dao.get("t3")?.pendingOperation)
    }

    @Test
    fun `la suppression est propagee au remote`() = runTest {
        val dao = FakeTaskDao()
        val remote = FakeRemoteSync()
        val repo = TaskRepository(dao, remote) { uid() }

        repo.addOrUpdateTask(TaskEntity(id = "t1", title = "A supprimer"))
        remote.docs["users/uid1/tasks"] = mutableMapOf(
            "t1" to (mapOf("id" to "t1", "title" to "A supprimer") to 1L)
        )

        repo.deleteTask("t1")
        repo.pushPendingChanges()

        assertFalse("document distant supprimé", remote.contains("users/uid1/tasks", "t1"))
        assertNull("ligne locale supprimée", dao.get("t1"))
    }

    @Test
    fun `le profil utilisateur est pousse vers users uid profile`() = runTest {
        val dao = FakeUserProfileDao()
        val remote = FakeRemoteSync()
        val repo = UserProfileRepository(dao, remote) { uid() }

        repo.saveProfile(firstName = "Hery", lastName = "Rabe", phone = "034 12 345 67", email = "h@example.mg")
        repo.pushPendingChanges()

        assertTrue(remote.contains("users/uid1/profile", "uid1"))
        val remoteProfile = remote.docs["users/uid1/profile"]!!["uid1"]!!.first
        assertEquals("Hery", remoteProfile["firstName"])
        assertTrue(dao.get("uid1")!!.isSynced)
    }

    @Test
    fun `le signalement est pousse avec les photos attachees`() = runTest {
        val dao = FakeSignalementDao()
        val remote = FakeRemoteSync()
        val session = InMemorySession().also { it.saveUid(uid()) }
        val repo = SignalementRepository(dao, remote, session)

        repo.save(
            SignalementEntity(
                id = "sig1",
                category = "VOIRIE",
                subcategory = "Eclairage",
                description = "Panne sujet",
                photoMediaIds = listOf("m1", "m2")
            )
        )
        repo.pushPendingChanges()

        assertTrue(remote.contains("users/uid1/signalements", "sig1"))
        val data = remote.docs["users/uid1/signalements"]!!["sig1"]!!.first
        assertEquals(listOf("m1", "m2"), data["photoMediaIds"])
    }

    @Test
    fun `les notifications locales sont synchronisees`() = runTest {
        val dao = FakeNotificationDao()
        val remote = FakeRemoteSync()
        val repo = NotificationRepository(dao, remote) { uid() }

        repo.save(NotificationEntity(id = "n1", title = "Titre", body = "Corps"))
        repo.pushPendingChanges()

        assertTrue(remote.contains("users/uid1/notifications", "n1"))
        assertEquals("Titre", remote.docs["users/uid1/notifications"]!!["n1"]!!.first["title"])
    }
}