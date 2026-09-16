package mg.iray.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mg.iray.app.di.ServiceLocator
import mg.iray.app.worker.enqueueImmediateSync
import mg.iray.app.worker.schedulePeriodicMediaUpload
import mg.iray.app.worker.schedulePeriodicSync

class IrayApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        schedulePeriodicSync(this)
        schedulePeriodicMediaUpload(this)

        appScope.launch {
            // Offline-first : référentiel local prêt avant toute sync réseau.
            ServiceLocator.territoryRepository().ensureLocalReferentiel()
            // premier lancement : création du uid anonyme unique, persisté localement
            ServiceLocator.authRepository().ensureUserId()
            // premier lancement hors-ligne : upgrade vers le vrai uid dès que possible
            ServiceLocator.authRepository().ensureRealUid()
            enqueueImmediateSync(this@IrayApp)
        }
    }
}