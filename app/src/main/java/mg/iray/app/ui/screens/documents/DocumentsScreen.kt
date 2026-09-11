package mg.iray.app.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.documents.DocumentUploadRow
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailsCatalog
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Documents à joindre" (étape 3 sur 5) — UN écran dynamique.
 *
 * Documents issus de [DemarcheDetailsCatalog] selon [demarcheId].
 * Best practice : fichiers joints hoistés ici, lignes stateless.
 */
data class DocumentsActions(
    val onBack: () -> Unit = {},
    val onAttachFile: (documentIndex: Int) -> Unit = {},
    val onNext: (Map<Int, String>) -> Unit = {},
)

@Composable
fun DocumentsScreen(
    demarcheId: String,
    modifier: Modifier = Modifier,
    actions: DocumentsActions = DocumentsActions(),
    externalAttached: Map<Int, String> = emptyMap(),
) {
    val details = DemarcheDetailsCatalog.get(demarcheId)
    val attachedFiles = remember { mutableStateMapOf<Int, String>() }
    // Fichiers confirmés sur la page d’upload.
    LaunchedEffect(externalAttached) {
        attachedFiles.putAll(externalAttached)
    }

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
                    contentDescription = stringResource(R.string.documents_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.documents_step),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.documents_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                details.documents.forEachIndexed { index, document ->
                    DocumentUploadRow(
                        documentName = document,
                        attachedFileName = attachedFiles[index],
                        onAttachClick = { actions.onAttachFile(index) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.documents_next),
                onClick = { actions.onNext(attachedFiles.toMap()) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun DocumentsScreenPreview() {
    IrayTheme {
        DocumentsScreen(demarcheId = "certificat-residence")
    }
}
