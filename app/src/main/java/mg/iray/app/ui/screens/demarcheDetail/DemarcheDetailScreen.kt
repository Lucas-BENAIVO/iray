package mg.iray.app.ui.screens.demarcheDetail

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.demarcheDetail.DemarcheDocumentsCard
import mg.iray.app.ui.components.demarcheDetail.DemarcheInfoBlock
import mg.iray.app.ui.components.demarcheDetail.DemarcheServiceCard
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary

/**
 * Écran détail d’une démarche — UN écran dynamique pour tous les cas.
 *
 * Contenu fourni par [DemarcheDetailsCatalog] selon [demarcheId].
 * Best practice : écran stateless, bouton commun réutilisé.
 */
data class DemarcheDetailActions(
    val onBack: () -> Unit = {},
    val onStartRequest: (String) -> Unit = {},
)

@Composable
fun DemarcheDetailScreen(
    demarcheId: String,
    modifier: Modifier = Modifier,
    actions: DemarcheDetailActions = DemarcheDetailActions(),
) {
    val details = DemarcheDetailsCatalog.get(demarcheId)

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
                    contentDescription = stringResource(R.string.demarche_detail_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = details.title,
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
            DemarcheServiceCard(service = details.service)

            Spacer(modifier = Modifier.height(20.dp))

            DemarcheDocumentsCard(documents = details.documents)

            Spacer(modifier = Modifier.height(20.dp))

            DemarcheInfoBlock(
                delay = details.delay,
                fees = details.fees,
            )

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.demarche_detail_cta),
                onClick = { actions.onStartRequest(details.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun DemarcheDetailScreenPreview() {
    IrayTheme {
        DemarcheDetailScreen(demarcheId = "certificat-residence")
    }
}
