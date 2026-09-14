package mg.iray.app.authflow

import kotlinx.coroutines.test.runTest
import mg.iray.app.fakes.FakeAuthSource
import mg.iray.app.fakes.FakeMediaDao
import mg.iray.app.fakes.FakeNotificationDao
import mg.iray.app.fakes.FakeSignalementDao
import mg.iray.app.fakes.FakeTaskDao
import mg.iray.app.fakes.FakeUserDao
import mg.iray.app.fakes.FakeUserProfileDao
import mg.iray.app.fakes.InMemorySession
import mg.iray.app.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserAuthenticationFlowTest {

    private val auth = FakeAuthSource()
    private val session = InMemorySession()
    private val userDao = FakeUserDao()
    private val profileDao = FakeUserProfileDao()

    private fun repo() = AuthRepository(
        auth, session, userDao, profileDao,
        FakeTaskDao(), FakeSignalementDao(), FakeMediaDao(), FakeNotificationDao()
    )

    @Test
    fun `premiere ouverture cree un uid stable et unique`() = runTest {
        val uid = repo().ensureUserId()

        assertNotNull(uid)
        assertFalse("le uid doit venir de Firebase", uid.startsWith("local-"))
        assertNotNull(session.getUid())
        assertEquals(uid, session.getUid())
        assertEquals(1, userDao.all().size)
    }

    @Test
    fun `les entrées futures reutilisent le meme uid meme hors ligne`() = runTest {
        val uid = repo().ensureUserId()

        // simulation d'un second lancement hors-ligne : signature fausse
        auth.fail = true
        auth.currentUid = null
        val secondLaunchRepo = repo()

        repeat(3) {
            assertEquals(uid, secondLaunchRepo.ensureUserId())
        }
        assertEquals("un seul utilisateur en base", 1, userDao.all().size)
    }

    @Test
    fun `premier lancement hors ligne cree un uid local puis le migre vers firebase`() = runTest {
        auth.fail = true
        val localUid = repo().ensureUserId()
        assertTrue(localUid.startsWith("local-"))

        // ajout d'infos hors-ligne sous cet uid local
        repo().addProfileInfo(firstName = "Hery", lastName = "Rabe", phone = "034 12 345 67")

        auth.fail = false
        val realUid = repo().ensureRealUid()

        assertFalse(realUid.startsWith("local-"))
        assertEquals("le stockage local doit pointer vers le vrai uid", realUid, session.getUid())
        assertNull("l'ancien uid local n'existe plus", userDao.get(localUid))
        assertNotNull("le profil est conservé et migré", profileDao.get(realUid))
        assertEquals("Hery", profileDao.get(realUid)?.firstName)
    }

    @Test
    fun `ajout d informations sauvegardé localement en offline-first`() = runTest {
        val uid = repo().ensureUserId()

        val profile = repo().addProfileInfo(
            firstName = "Hery",
            lastName = "Rabe",
            phone = "034 12 345 67",
            email = "hery.rabe@example.mg"
        )

        assertEquals(uid, profile.uid)
        assertFalse("profil pas encore synchronisé", profile.isSynced)
        assertEquals("UPDATE", profile.pendingOperation)
        assertEquals("Hery", profileDao.get(uid)?.firstName)
        assertEquals("hery.rabe@example.mg", profileDao.get(uid)?.email)
    }

    @Test
    fun `lier un email conserve le meme uid`() = runTest {
        auth.fail = false
        val uid = repo().ensureUserId()
        auth.currentUid = uid

        val linked = repo().linkWithEmail("hery.rabe@example.mg", "mot-de-passe")

        assertEquals("le uid ne change pas après link", uid, linked.uid)
        assertFalse(linked.isAnonymous)
        assertEquals("hery.rabe@example.mg", linked.email)
        assertEquals(uid, session.getUid())
    }
}