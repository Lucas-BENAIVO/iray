package mg.iray.app.ui.components.zone

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import mg.iray.app.map.MapOfflinePackager
import mg.iray.app.ui.theme.IrayTheme
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

/** Centre par défaut - Antananarivo. */
data class MapLatLng(val latitude: Double, val longitude: Double)

val DefaultMapLatLng = MapLatLng(-18.8792, 47.5079)

private const val TAG = "ZoneMapPreview"

/**
 * Carte MapLibre + OpenStreetMap (style asset, packs hors-ligne possibles).
 * Pas de Modifier.clip() (casse souvent le rendu GL).
 */
@Composable
fun ZoneMapPreview(
    latitude: Double?,
    longitude: Double?,
    onPositionChange: (lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier,
    heightDp: Int = 300,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).also { it.onCreate(Bundle()) }
    }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    val onPositionChangeState = remember { mutableStateOf(onPositionChange) }
    onPositionChangeState.value = onPositionChange

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        val state = lifecycleOwner.lifecycle.currentState
        if (state.isAtLeast(Lifecycle.State.STARTED)) mapView.onStart()
        if (state.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
            } catch (_: Exception) {
                // déjà détruit via lifecycle
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .border(1.dp, Color(0xFFD0CBC3), RoundedCornerShape(12.dp)),
    )

    LaunchedEffect(mapView) {
        mapView.addOnDidFailLoadingMapListener { error ->
            Log.e(TAG, "Échec chargement carte: $error")
        }
        mapView.addOnDidFinishLoadingStyleListener {
            Log.d(TAG, "Style OSM chargé")
        }
        mapView.getMapAsync { map ->
            val initial = if (latitude != null && longitude != null) {
                LatLng(latitude, longitude)
            } else {
                LatLng(DefaultMapLatLng.latitude, DefaultMapLatLng.longitude)
            }
            map.cameraPosition = CameraPosition.Builder()
                .target(initial)
                .zoom(16.0)
                .build()
            map.uiSettings.isCompassEnabled = false
            map.uiSettings.isRotateGesturesEnabled = false
            map.uiSettings.isAttributionEnabled = true
            map.uiSettings.isLogoEnabled = false
            map.addOnMapClickListener { point ->
                onPositionChangeState.value(point.latitude, point.longitude)
                true
            }
            // Même URI que les packs Offline (asset://…)
            map.setStyle(MapOfflinePackager.STYLE_URI) {
                mapLibreMap = map
                if (latitude != null && longitude != null) {
                    marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .title("Toerana"),
                    )
                }
            }
        }
    }

    LaunchedEffect(latitude, longitude, mapLibreMap) {
        val map = mapLibreMap ?: return@LaunchedEffect
        if (latitude == null || longitude == null) return@LaunchedEffect
        val latLng = LatLng(latitude, longitude)
        val current = marker
        if (current == null) {
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Toerana"),
            )
        } else {
            current.position = latLng
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0))
    }
}

@Preview(showBackground = true)
@Composable
private fun ZoneMapPreviewPreview() {
    IrayTheme {
        ZoneMapPreview(
            latitude = DefaultMapLatLng.latitude,
            longitude = DefaultMapLatLng.longitude,
            onPositionChange = { _, _ -> },
        )
    }
}
