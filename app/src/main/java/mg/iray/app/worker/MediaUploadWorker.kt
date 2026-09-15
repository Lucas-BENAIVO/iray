package mg.iray.app.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import mg.iray.app.di.ServiceLocator
import java.util.concurrent.TimeUnit

class MediaUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (ServiceLocator.session.getUid() == null) return Result.failure()
        return try {
            ServiceLocator.mediaRepository().uploadPending()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

private const val MEDIA_UPLOAD_WORK_NAME = "iray_media_upload"

private fun mediaConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

// Un-shot : à appeler juste après un enregistrement local de médias
fun enqueueMediaUpload(context: Context) {
    val request = OneTimeWorkRequestBuilder<MediaUploadWorker>()
        .setConstraints(mediaConstraints())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(MEDIA_UPLOAD_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
}

// File d'attente périodique (reprise des échecs)
fun schedulePeriodicMediaUpload(context: Context) {
    val request = PeriodicWorkRequestBuilder<MediaUploadWorker>(30, TimeUnit.MINUTES)
        .setConstraints(mediaConstraints())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(MEDIA_UPLOAD_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
}