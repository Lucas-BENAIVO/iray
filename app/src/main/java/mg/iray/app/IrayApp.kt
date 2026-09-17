package mg.iray.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mg.iray.app.di.ServiceLocator
import mg.iray.app.map.MapOfflinePackager
import mg.iray.app.worker.enqueueImmediateSync
import mg.iray.app.worker.schedulePeriodicMediaUpload
import mg.iray.app.worker.schedulePeriodicSync
import okhttp3.OkHttpClient
import org.maplibre.android.MapLibre
import org.maplibre.android.module.http.HttpRequestUtil
import java.util.concurrent.TimeUnit

class IrayApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        // OSM exige un User-Agent identifiable pour servir les tuiles.
        HttpRequestUtil.setOkHttpClient(
            OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request().newBuilder()
                            .header("User-Agent", "IrayAndroid/1.0 (mg.iray.app; contact: iray-app)")
                            .build(),
                    )
                }
                .build(),
        )
        ServiceLocator.init(this)
        schedulePeriodicSync(this)
        schedulePeriodicMediaUpload(this)

        // Précharge carte Antananarivo + aperçu Madagascar (1re fois = Internet).
        MapOfflinePackager.ensureOfflinePacks(this)

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