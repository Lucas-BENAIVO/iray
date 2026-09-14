package mg.iray.app.ui.screens.success

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.success.SuccessCheckmark
import mg.iray.app.ui.components.success.SuccessSummaryCard
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Profil créé !" — entre zone et welcome.
 *
 * Affiche les vraies données saisies ([fullName], [zoneLabel]).
 * Best practice : stateless, bouton commun réutilisé.
 */
data class SuccessActions(
    val onStart: () -> Unit = {},
)

@Composable
fun SuccessScreen(
    fullName: String,
    zoneLabel: String,
    modifier: Modifier = Modifier,
    actions: SuccessActions = SuccessActions(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SuccessCheckmark()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.success_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = mg.iray.app.ui.theme.IrayDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                ),
                color = FlagGreen,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            mg.iray.app.ui.components.FlagAccentBar(
                modifier = Modifier.fillMaxWidth(0.28f),
                height = 3.dp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.success_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            SuccessSummaryCard(
                fullName = fullName,
                zoneLabel = zoneLabel,
            )

            Spacer(modifier = Modifier.height(40.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.success_cta),
                onClick = actions.onStart,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SuccessScreenPreview() {
    IrayTheme {
        SuccessScreen(
            fullName = "Jean Rakoto",
            zoneLabel = "Antananarivo, Andohalo",
        )
    }
}
