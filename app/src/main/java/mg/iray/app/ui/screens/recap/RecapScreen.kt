package mg.iray.app.ui.screens.recap

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.recap.RecapDetailsBlock
import mg.iray.app.ui.components.recap.RecapDocumentsBlock
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailsCatalog
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage

/**
 * Écran "Récapitulatif" (étape 5 sur 5) - UN écran dynamique.
 *
 * Détails issus de [DemarcheDetailsCatalog], fichiers joints réels
 * ([attachedFileNames], sinon noms des pièces requises).
 * Best practice : écran stateless, bouton commun réutilisé.
 */
data class RecapActions(
    val onBack: () -> Unit = {},
    val onSubmit: (String) -> Unit = {},
)

@Composable
fun RecapScreen(
    demarcheId: String,
    modifier: Modifier = Modifier,
    actions: RecapActions = RecapActions(),
    attachedFileNames: List<String> = emptyList(),
) {
    val details = DemarcheDetailsCatalog.get(demarcheId)
    val shownFiles = attachedFileNames.ifEmpty { details.documents }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.recap_title),
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
            RecapDetailsBlock(
                demarche = details.title,
                service = details.service,
                fees = details.fees,
                delay = details.delay,
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecapDocumentsBlock(fileNames = shownFiles)

            Spacer(modifier = Modifier.height(32.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.recap_cta),
                onClick = { actions.onSubmit(details.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun RecapScreenPreview() {
    IrayTheme {
        RecapScreen(demarcheId = "certificat-residence")
    }
}
