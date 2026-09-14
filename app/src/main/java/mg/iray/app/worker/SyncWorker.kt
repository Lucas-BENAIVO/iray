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

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // uid requis (créé dès le premier lancement par AuthRepository)
        if (ServiceLocator.session.getUid() == null) return Result.failure()

        return try {
            ServiceLocator.syncableRepositories().forEach { repository ->
                repository.pushPendingChanges()
                repository.pullRemoteChanges()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

private const val SYNC_WORK_NAME = "iray_data_sync"
private const val PERIODIC_SYNC_WORK_NAME = "iray_periodic_sync"

private fun syncConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

// Sync périodique (WorkManager)
fun schedulePeriodicSync(context: Context) {
    val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(syncConstraints())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(PERIODIC_SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
}

// Sync immédiate (ex. au retour du réseau ou après une écriture importante)
fun enqueueImmediateSync(context: Context) {
    val request = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(syncConstraints())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(SYNC_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
}