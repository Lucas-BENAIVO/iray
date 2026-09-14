package mg.iray.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import mg.iray.app.di.ServiceLocator

/**
 * Premier lancement hors-ligne => uid local "local-...".
 * Dès que le réseau revient, ce worker signe anonymement et migre les
 * données locales vers le vrai uid Firebase (les entrées futures l'utilisent).
 */
class AuthSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            ServiceLocator.authRepository().ensureRealUid()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}