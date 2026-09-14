package mg.iray.app.e2e

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
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
import mg.iray.app.entity.MediaUploadState
import mg.iray.app.repository.MediaRepository
import mg.iray.app.worker.MediaUploadWorker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E de la gestion locale des médias et de leur envoi vers Firebase Storage,
 * orchestrée par WorkManager.
 */
@RunWith(AndroidJUnit4::class)
class MediaUploadWorkerEndToEndTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val uploader = FakeMediaUploader()
    private val remote = FakeRemoteSync()

    @Before
    fun init() {
        ServiceLocator.db = AppDatabase.inMemory(context)
        ServiceLocator.session = InMemorySession()
        ServiceLocator.authSource = FakeAuthSource()
        ServiceLocator.remoteSync = remote
        ServiceLocator.mediaUploader = uploader

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
    fun mediaUploadWorker_televerseLesMediasLocaux() = runBlocking {
        val repo = MediaRepository(ServiceLocator.db.mediaDao(), uploader) { "uid1" }
        val media = repo.saveLocally("content://tmp/photo.jpg", "image/jpeg", "SIGNALEMENT", "sig1")
        assertEquals(MediaUploadState.PENDING, ServiceLocator.db.mediaDao().get(media.id)?.uploadState)

        val request = OneTimeWorkRequestBuilder<MediaUploadWorker>().build()
        WorkManager.getInstance(context).enqueue(request).result.get()

        val info = WorkManager.getInstance(context)
            .getWorkInfosByTag(MediaUploadWorker::class.java.name).get().first()
        assertEquals(WorkInfo.State.SUCCEEDED, info.state)

        val uploaded = ServiceLocator.db.mediaDao().get(media.id)!!
        assertEquals(MediaUploadState.UPLOADED, uploaded.uploadState)
        assertNotNull(uploaded.remoteUrl)
        assertEquals("https://firebasestorage.example.com/${media.id}", uploaded.remoteUrl)
        assertEquals(0, ServiceLocator.db.mediaDao().getPendingUploads("uid1").size)
    }
}