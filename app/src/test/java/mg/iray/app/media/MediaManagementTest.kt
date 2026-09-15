package mg.iray.app.media

import kotlinx.coroutines.test.runTest
import mg.iray.app.entity.MediaUploadState
import mg.iray.app.fakes.FakeMediaDao
import mg.iray.app.fakes.FakeMediaUploader
import mg.iray.app.repository.MediaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaManagementTest {

    private fun uid() = "uid1"

    @Test
    fun `un media sauvegarde localement demarre en PENDING`() = runTest {
        val dao = FakeMediaDao()
        val uploader = FakeMediaUploader()
        val repo = MediaRepository(dao, uploader) { uid() }

        val media = repo.saveLocally("file:///tmp/photo.jpg", "image/jpeg", "SIGNALEMENT", "sig1")

        assertNotNull(media.id)
        assertEquals("file:///tmp/photo.jpg", media.localUri)
        assertEquals(MediaUploadState.PENDING, dao.get(media.id)?.uploadState)
        assertTrue(dao.all().isNotEmpty())
    }

    @Test
    fun `l upload envoie le fichier et marque le media UPLOADED avec son URL`() = runTest {
        val dao = FakeMediaDao()
        val uploader = FakeMediaUploader()
        val repo = MediaRepository(dao, uploader) { uid() }

        val media = repo.saveLocally("content://tmp/photo.jpg", "image/jpeg", "SIGNALEMENT", "sig1")

        val uploaded = repo.uploadPending()

        assertEquals(1, uploaded)
        assertEquals(listOf(media.id), uploader.uploadedIds)
        assertEquals(MediaUploadState.UPLOADED, dao.get(media.id)?.uploadState)
        assertNotNull(dao.get(media.id)?.remoteUrl)
        assertEquals("https://firebasestorage.example.com/${media.id}", dao.get(media.id)?.remoteUrl)
    }

    @Test
    fun `un echec d upload est retente au cycle suivant`() = runTest {
        val dao = FakeMediaDao()
        val uploader = FakeMediaUploader()
        val repo = MediaRepository(dao, uploader) { uid() }

        val media = repo.saveLocally("file:///tmp/photo.jpg", "image/jpeg")

        uploader.fail = true
        val firstRun = repo.uploadPending()
        assertEquals(0, firstRun)
        assertEquals("l'échec bascule en FAILED", MediaUploadState.FAILED, dao.get(media.id)?.uploadState)

        uploader.fail = false
        val secondRun = repo.uploadPending()
        assertEquals(1, secondRun)
        assertEquals(MediaUploadState.UPLOADED, dao.get(media.id)?.uploadState)
        assertNotNull(dao.get(media.id)?.remoteUrl)
    }

    @Test
    fun `un media deja UPLOADED nest pas re-envoye`() = runTest {
        val dao = FakeMediaDao()
        val uploader = FakeMediaUploader()
        val repo = MediaRepository(dao, uploader) { uid() }

        repo.saveLocally("file:///tmp/a.jpg", "image/jpeg")
        repo.saveLocally("file:///tmp/b.jpg", "image/jpeg")
        repo.uploadPending()

        val secondRun = repo.uploadPending()

        assertEquals("rien à re-téléverser", 0, secondRun)
        assertEquals(2, uploader.uploadedIds.size)
    }
}