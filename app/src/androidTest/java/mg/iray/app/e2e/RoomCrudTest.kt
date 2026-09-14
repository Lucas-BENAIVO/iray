package mg.iray.app.e2e

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mg.iray.app.db.AppDatabase
import mg.iray.app.entity.MediaEntity
import mg.iray.app.entity.MediaUploadState
import mg.iray.app.entity.TaskEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomCrudTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertionEtObservationDepuisRoom() = runBlocking {
        db.taskDao().upsert(
            TaskEntity(id = "t1", userId = "u1", title = "Tache 1", pendingOperation = "UPDATE")
        )

        val tasks = db.taskDao().observeTasks("u1").first()

        assertEquals(1, tasks.size)
        assertEquals("Tache 1", tasks[0].title)
    }

    @Test
    fun lesTachesSupprimeesSontFiltrees() = runBlocking {
        db.taskDao().upsert(TaskEntity(id = "t1", userId = "u1", title = "x", isDeleted = true))

        assertEquals(0, db.taskDao().observeTasks("u1").first().size)
    }

    @Test
    fun filtrageDesUploadsEnAttente() = runBlocking {
        db.mediaDao().upsert(MediaEntity(id = "m1", userId = "u1", localUri = "file:///a", uploadState = MediaUploadState.PENDING))
        db.mediaDao().upsert(MediaEntity(id = "m2", userId = "u1", localUri = "file:///b", uploadState = MediaUploadState.UPLOADED))
        db.mediaDao().upsert(MediaEntity(id = "m3", userId = "u1", localUri = "file:///c", uploadState = MediaUploadState.FAILED))

        val pending = db.mediaDao().getPendingUploads("u1")

        assertEquals(setOf("m1", "m3"), pending.map { it.id }.toSet())
    }

    @Test
    fun rewriteUidMigreLesDonnees() = runBlocking {
        db.taskDao().upsert(TaskEntity(id = "t1", userId = "u1", title = "x"))
        db.signalementDao().upsert(
            mg.iray.app.entity.SignalementEntity(id = "s1", userId = "u1")
        )

        db.taskDao().rewriteUid("u1", "u-new")
        db.signalementDao().rewriteUid("u1", "u-new")

        assertEquals("u-new", db.taskDao().get("t1")!!.userId)
        assertEquals("u-new", db.signalementDao().get("s1")!!.userId)
        assertNull(db.signalementDao().get("s1")?.pendingOperation)
    }

    @Test
    fun markUploadedPositionneUrLetat() = runBlocking {
        db.mediaDao().upsert(MediaEntity(id = "m1", userId = "u1", localUri = "file:///a"))

        db.mediaDao().markUploaded("m1", "https://storage.example/m1")

        val media = db.mediaDao().get("m1")!!
        assertEquals(MediaUploadState.UPLOADED, media.uploadState)
        assertEquals("https://storage.example/m1", media.remoteUrl)
        assertTrue(media.isSynced)
    }
}