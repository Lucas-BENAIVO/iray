package mg.iray.app.ui.components.zone

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import mg.iray.app.ui.theme.IrayTheme

/** Centre par défaut — Antananarivo. */
val DefaultMapLatLng = LatLng(-18.8792, 47.5079)

/**
 * Carte interactive Google Maps — pin + tap pour choisir la position.
 */
@Composable
fun ZoneMapPreview(
    latitude: Double?,
    longitude: Double?,
    onPositionChange: (lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier,
    heightDp: Int = 220,
) {
    val target = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        DefaultMapLatLng
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, 15f)
    }
    val markerState = remember { MarkerState(position = target) }

    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            val latLng = LatLng(latitude, longitude)
            markerState.position = latLng
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 16f),
            )
        }
    }

    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onPositionChange(latLng.latitude, latLng.longitude)
        },
    ) {
        if (latitude != null && longitude != null) {
            Marker(
                state = markerState,
                title = "Toerana",
            )
        }
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
