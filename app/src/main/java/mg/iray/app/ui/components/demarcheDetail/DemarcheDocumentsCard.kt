package mg.iray.app.ui.components.demarcheDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.DividerSubtle
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Carte "Documents nécessaires" — lignes "» + libellé" séparées.
 *
 * Best practice : stateless, liste en paramètre.
 */
@Composable
fun DemarcheDocumentsCard(
    documents: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.demarche_detail_documents_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BrandWhite,
            border = BorderStroke(1.dp, OutlineOnWhite),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                documents.forEachIndexed { index, document ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = "»",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = ZoneMapPin,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = document,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                    if (index < documents.lastIndex) {
                        HorizontalDivider(
                            color = DividerSubtle,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DemarcheDocumentsCardPreview() {
    IrayTheme {
        DemarcheDocumentsCard(
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Justificatif de domicile",
            ),
        )
    }
}
