package mg.iray.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class DevicePosition(
    val latitude: Double,
    val longitude: Double,
    val addressLabel: String? = null,
)

fun Context.hasLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
suspend fun Context.fetchCurrentPosition(): DevicePosition? {
    if (!hasLocationPermission()) return null
    val client = LocationServices.getFusedLocationProviderClient(this)
    val location = runCatching {
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token,
        ).await()
    }.getOrNull() ?: runCatching {
        client.lastLocation.await()
    }.getOrNull() ?: return null

    val label = reverseGeocode(location.latitude, location.longitude)
    return DevicePosition(
        latitude = location.latitude,
        longitude = location.longitude,
        addressLabel = label,
    )
}

suspend fun Context.reverseGeocode(latitude: Double, longitude: Double): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            if (!Geocoder.isPresent()) return@runCatching null
            val geocoder = Geocoder(this@reverseGeocode, Locale.getDefault())
            @Suppress("DEPRECATION")
            val results = geocoder.getFromLocation(latitude, longitude, 1)
            val addr = results?.firstOrNull() ?: return@runCatching null
            listOfNotNull(
                addr.thoroughfare,
                addr.subLocality,
                addr.locality,
                addr.adminArea,
            ).distinct().joinToString(", ").ifBlank { addr.getAddressLine(0) }
        }.getOrNull()
    }
