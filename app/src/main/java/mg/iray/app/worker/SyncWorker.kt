package mg.iray.app.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mg.iray.app.db.AppDatabase
import mg.iray.app.repository.TaskRepository
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure()
        val repository = TaskRepository(db.taskDao(), FirebaseFirestore.getInstance(), userId)

        return try {
            repository.pushPendingChanges()
            repository.pullRemoteChanges()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Planification : sync périodique + sync immédiate quand le réseau revient
fun scheduleSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork("task_sync", ExistingPeriodicWorkPolicy.KEEP, request)
}