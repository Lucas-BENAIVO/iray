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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IraySecondaryButton
import mg.iray.app.ui.components.confirmation.ConfirmationDossierCard
import mg.iray.app.ui.components.success.SuccessCheckmark
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Signalement envoyé !" — fin du parcours signalement.
 *
 * N° SIG stable, date du jour, timeline. Best practice : écran stateless,
 * composants communs (check, dossier, timeline, boutons) réutilisés.
 */
data class SignalementSuccessActions(
    val onBack: () -> Unit = {},
    val onViewReports: () -> Unit = {},
    val onHome: () -> Unit = {},
)

@Composable
fun SignalementSuccessScreen(
    categoryId: String,
    subcategory: String,
    modifier: Modifier = Modifier,
    actions: SignalementSuccessActions = SignalementSuccessActions(),
) {
    // N° stable (démo — le backend l’attribuera).
    val sigNumber = remember(categoryId, subcategory) {
        "SIG-2026-%06d".format(
            (abs("$categoryId/$subcategory".hashCode()) % 900000) + 100000,
        )
    }
    val receivedAt = remember {
        SimpleDateFormat("d MMM yyyy - HH:mm", Locale.FRENCH).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = actions.onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(
                        R.string.signalement_success_back_cd,
                    ),
                    tint = TextPrimary,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SuccessCheckmark()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.signalement_success_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = FlagGreen,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.signalement_success_subtitle_format,
                    subcategory.replaceFirstChar { it.lowercase() },
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            ConfirmationDossierCard(dossierNumber = sigNumber)

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.signalement_success_received_label),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TextPrimary,
                )
                Text(
                    text = receivedAt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_view_reports),
                onClick = actions.onViewReports,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            IraySecondaryButton(
                label = stringResource(R.string.signalement_back_home),
                onClick = actions.onHome,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementSuccessScreenPreview() {
    IrayTheme {
        SignalementSuccessScreen(
            categoryId = "electricite",
            subcategory = "Coupure d’électricité",
        )
    }
}
