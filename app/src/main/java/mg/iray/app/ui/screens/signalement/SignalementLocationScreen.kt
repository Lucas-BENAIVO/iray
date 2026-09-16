package mg.iray.app.ui.screens.signalement

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mg.iray.app.R
import mg.iray.app.location.fetchCurrentPosition
import mg.iray.app.location.hasLocationPermission
import mg.iray.app.location.reverseGeocode
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.components.zone.DefaultMapLatLng
import mg.iray.app.ui.components.zone.ZoneMapPreview
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran localisation du signalement - carte réelle + GPS + adresse.
 */
data class SignalementLocationActions(
    val onBack: () -> Unit = {},
    val onContinue: (address: String, latitude: Double, longitude: Double) -> Unit = { _, _, _ -> },
)

@Composable
fun SignalementLocationScreen(
    initialAddress: String,
    modifier: Modifier = Modifier,
    actions: SignalementLocationActions = SignalementLocationActions(),
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var address by rememberSaveable { mutableStateOf(initialAddress) }
    var latitude by rememberSaveable { mutableStateOf(initialLatitude) }
    var longitude by rememberSaveable { mutableStateOf(initialLongitude) }
    var locating by remember { mutableStateOf(false) }

    fun applyPosition(lat: Double, lng: Double, label: String? = null) {
        latitude = lat
        longitude = lng
        if (!label.isNullOrBlank()) {
            address = label
        }
    }

    fun requestGps() {
        locating = true
        scope.launch {
            val pos = context.fetchCurrentPosition()
            locating = false
            if (pos == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.signalement_location_unavailable),
                    Toast.LENGTH_SHORT,
                ).show()
                // Fallback centre Tana si pas encore de pin.
                if (latitude == null || longitude == null) {
                    applyPosition(DefaultMapLatLng.latitude, DefaultMapLatLng.longitude)
                }
            } else {
                applyPosition(pos.latitude, pos.longitude, pos.addressLabel)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val ok = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (ok) {
            requestGps()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.signalement_location_permission_denied),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun onUseMyPosition() {
        if (context.hasLocationPermission()) {
            requestGps()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val canContinue = address.isNotBlank() && latitude != null && longitude != null
    val coordsHint = if (latitude != null && longitude != null) {
        stringResource(
            R.string.signalement_coords_label,
            latitude!!,
            longitude!!,
        )
    } else {
        stringResource(R.string.signalement_coords_hint)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.signalement_location_title),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
        ) {
            ZoneMapPreview(
                latitude = latitude,
                longitude = longitude,
                onPositionChange = { lat, lng ->
                    applyPosition(lat, lng)
                    scope.launch {
                        val label = context.reverseGeocode(lat, lng)
                        if (!label.isNullOrBlank() && address.isBlank()) {
                            address = label
                        } else if (!label.isNullOrBlank()) {
                            // Enrichit sans écraser une adresse déjà saisie par l’utilisateur
                            // uniquement si c’était le préremplissage profil.
                        }
                        if (!label.isNullOrBlank()) address = label
                    }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = coordsHint,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            IrayPrimaryButton(
                label = if (locating) {
                    stringResource(R.string.signalement_locating)
                } else {
                    stringResource(R.string.signalement_use_position)
                },
                onClick = { onUseMyPosition() },
                enabled = !locating,
                containerColor = FlagRed,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileTextField(
                value = address,
                onValueChange = { address = it },
                label = stringResource(R.string.signalement_address_label),
                hint = stringResource(R.string.signalement_address_hint),
                leadingIcon = Icons.Filled.LocationOn,
                singleLine = false,
                accentColor = FlagRed,
            )

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_continue),
                onClick = {
                    actions.onContinue(address.trim(), latitude!!, longitude!!)
                },
                enabled = canContinue,
                containerColor = FlagRed,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementLocationScreenPreview() {
    IrayTheme {
        SignalementLocationScreen(
            initialAddress = "Fokontany Andohalo, Antananarivo",
            initialLatitude = DefaultMapLatLng.latitude,
            initialLongitude = DefaultMapLatLng.longitude,
        )
    }
}
