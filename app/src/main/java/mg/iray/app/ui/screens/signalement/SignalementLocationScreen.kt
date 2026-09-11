package mg.iray.app.ui.screens.signalement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.components.zone.ZoneMapPreview
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Écran localisation du signalement — carte + position + adresse.
 *
 * Best practice : adresse hoistée ici, composants partagés réutilisés.
 */
data class SignalementLocationActions(
    val onBack: () -> Unit = {},
    val onUsePosition: () -> Unit = {},
    val onContinue: (address: String) -> Unit = {},
)

@Composable
fun SignalementLocationScreen(
    initialAddress: String,
    modifier: Modifier = Modifier,
    actions: SignalementLocationActions = SignalementLocationActions(),
) {
    var address by rememberSaveable { mutableStateOf(initialAddress) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 24.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = actions.onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.signalement_location_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.signalement_location_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
        ) {
            ZoneMapPreview()

            Spacer(modifier = Modifier.height(16.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_use_position),
                onClick = actions.onUsePosition,
                containerColor = ZoneMapPin,
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
            )

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_continue),
                onClick = { actions.onContinue(address.trim()) },
                enabled = address.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementLocationScreenPreview() {
    IrayTheme {
        SignalementLocationScreen(initialAddress = "Fokontany Andohalo, Antananarivo")
    }
}
