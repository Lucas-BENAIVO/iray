package mg.iray.app.map

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionError
import org.maplibre.android.offline.OfflineRegionStatus
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import java.nio.charset.StandardCharsets

/**
 * Précharge des packs carte MapLibre pour usage hors-ligne.
 *
 * Premier téléchargement = Internet requis.
 * Ensuite la zone Antananarivo (+ aperçu Madagascar) reste disponible offline.
 */
object MapOfflinePackager {

    const val STYLE_URI = "asset://map/osm_raster_style.json"

    private const val TAG = "MapOfflinePackager"
    private const val PREFS = "iray_map_offline"
    private const val KEY_READY = "packs_ready_v1"

    private const val META_MG = "iray-offline-madagascar-v1"
    private const val META_TANA = "iray-offline-antananarivo-v1"

    /** Aperçu pays (peu de tuiles). */
    private val MADAGASCAR_BOUNDS = LatLngBounds.Builder()
        .include(LatLng(-25.6, 43.2))
        .include(LatLng(-11.9, 50.5))
        .build()

    /** Grand Antananarivo - rues visibles. */
    private val ANTANANARIVO_BOUNDS = LatLngBounds.Builder()
        .include(LatLng(-19.02, 47.38))
        .include(LatLng(-18.78, 47.62))
        .build()

    sealed class Status {
        data object Checking : Status()
        data object Ready : Status()
        data object NeedsNetwork : Status()
        data class Downloading(val percent: Int, val label: String) : Status()
        data class Error(val message: String) : Status()
    }

    fun isReady(context: Context): Boolean =
        prefs(context).getBoolean(KEY_READY, false)

    fun ensureOfflinePacks(context: Context, onStatus: (Status) -> Unit = {}) {
        val app = context.applicationContext
        onStatus(Status.Checking)

        if (isReady(app)) {
            onStatus(Status.Ready)
            return
        }

        if (!hasInternet(app)) {
            onStatus(Status.NeedsNetwork)
            return
        }

        val offlineManager = OfflineManager.getInstance(app)
        offlineManager.setOfflineMapboxTileCountLimit(80_000)
        offlineManager.setMaximumAmbientCacheSize(
            120L * 1024L * 1024L,
            object : OfflineManager.FileSourceCallback {
                override fun onSuccess() = Unit
                override fun onError(message: String) {
                    Log.w(TAG, "cache size: $message")
                }
            },
        )

        offlineManager.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>?) {
                val existing = offlineRegions.orEmpty()
                val names = existing.mapNotNull { metaName(it.metadata) }.toSet()
                Log.d(TAG, "Packs existants: $names")

                val missing = buildList {
                    if (META_MG !in names) {
                        add(
                            PackSpec(
                                name = META_MG,
                                label = "Madagasikara",
                                bounds = MADAGASCAR_BOUNDS,
                                minZoom = 4.0,
                                maxZoom = 8.0,
                            ),
                        )
                    }
                    if (META_TANA !in names) {
                        add(
                            PackSpec(
                                name = META_TANA,
                                label = "Antananarivo",
                                bounds = ANTANANARIVO_BOUNDS,
                                minZoom = 11.0,
                                maxZoom = 15.0,
                            ),
                        )
                    }
                }

                if (missing.isEmpty()) {
                    markReady(app)
                    onStatus(Status.Ready)
                    return
                }

                downloadSequentially(app, offlineManager, missing, 0, onStatus)
            }

            override fun onError(error: String) {
                Log.e(TAG, "listOfflineRegions: $error")
                onStatus(Status.Error(error))
            }
        })
    }

    private fun downloadSequentially(
        context: Context,
        offlineManager: OfflineManager,
        packs: List<PackSpec>,
        index: Int,
        onStatus: (Status) -> Unit,
    ) {
        if (index >= packs.size) {
            markReady(context)
            onStatus(Status.Ready)
            return
        }

        val pack = packs[index]
        val pixelRatio = context.resources.displayMetrics.density
        val definition = OfflineTilePyramidRegionDefinition(
            STYLE_URI,
            pack.bounds,
            pack.minZoom,
            pack.maxZoom,
            pixelRatio,
        )
        val metadata = pack.name.toByteArray(StandardCharsets.UTF_8)

        onStatus(Status.Downloading(percent = 0, label = pack.label))

        offlineManager.createOfflineRegion(
            definition,
            metadata,
            object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(offlineRegion: OfflineRegion) {
                    offlineRegion.setObserver(object : OfflineRegion.OfflineRegionObserver {
                        override fun onStatusChanged(status: OfflineRegionStatus) {
                            val pct = if (status.requiredResourceCount > 0) {
                                ((100.0 * status.completedResourceCount) /
                                    status.requiredResourceCount).toInt().coerceIn(0, 100)
                            } else {
                                0
                            }
                            onStatus(Status.Downloading(percent = pct, label = pack.label))
                            if (status.isComplete) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE)
                                Log.i(TAG, "Pack OK: ${pack.name}")
                                downloadSequentially(
                                    context,
                                    offlineManager,
                                    packs,
                                    index + 1,
                                    onStatus,
                                )
                            }
                        }

                        override fun onError(error: OfflineRegionError) {
                            Log.e(TAG, "Pack ${pack.name}: ${error.reason} ${error.message}")
                            offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE)
                            onStatus(Status.Error("${pack.label}: ${error.message}"))
                        }

                        override fun mapboxTileCountLimitExceeded(limit: Long) {
                            Log.e(TAG, "Limite tuiles dépassée: $limit")
                            offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE)
                            onStatus(Status.Error("Limite tuiles hors-ligne atteinte"))
                        }
                    })
                    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
                }

                override fun onError(error: String) {
                    Log.e(TAG, "createOfflineRegion ${pack.name}: $error")
                    onStatus(Status.Error(error))
                }
            },
        )
    }

    private data class PackSpec(
        val name: String,
        val label: String,
        val bounds: LatLngBounds,
        val minZoom: Double,
        val maxZoom: Double,
    )

    private fun metaName(bytes: ByteArray?): String? =
        bytes?.toString(StandardCharsets.UTF_8)?.takeIf { it.isNotBlank() }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun markReady(context: Context) {
        prefs(context).edit().putBoolean(KEY_READY, true).apply()
    }

    private fun hasInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
