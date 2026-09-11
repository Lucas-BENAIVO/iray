package mg.iray.app.ui.components.recap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Détails de la demande — lignes libellé gris + valeur grasse.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun RecapDetailsBlock(
    demarche: String,
    service: String,
    fees: String,
    delay: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recap_details_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailLine(
            label = stringResource(R.string.recap_demarche_label),
            value = demarche,
        )
        Spacer(modifier = Modifier.height(10.dp))
        DetailLine(
            label = stringResource(R.string.recap_service_label),
            value = service,
        )
        Spacer(modifier = Modifier.height(10.dp))
        DetailLine(
            label = stringResource(R.string.recap_fees_label),
            value = fees,
        )
        Spacer(modifier = Modifier.height(10.dp))
        DetailLine(
            label = stringResource(R.string.recap_delay_label),
            value = delay,
        )
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = TextPrimary,
        )
    }
}

/**
 * Documents joints — lignes check vert + nom du fichier.
 *
 * Best practice : stateless, liste en paramètre.
 */
@Composable
fun RecapDocumentsBlock(
    fileNames: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recap_docs_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        fileNames.forEach { fileName ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = FlagGreen,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecapBlocksPreview() {
    IrayTheme {
        Column {
            RecapDetailsBlock(
                demarche = "Certificat de résidence",
                service = "Administration locale",
                fees = "Gratuit",
                delay = "3 à 7 jours ouvrés",
            )
        }
    }
}
