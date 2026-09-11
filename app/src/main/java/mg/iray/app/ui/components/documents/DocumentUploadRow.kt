package mg.iray.app.ui.components.documents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Ligne document à joindre — "» + icône + nom + Joindre un fichier".
 *
 * Affiche le nom du fichier joint quand présent.
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun DocumentUploadRow(
    documentName: String,
    attachedFileName: String?,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onAttachClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = BrandWhite,
        border = BorderStroke(1.dp, OutlineOnWhite),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = "»",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = ZoneMapPin,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = ZoneMapPin.copy(alpha = 0.14f),
                modifier = Modifier.size(40.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = null,
                        tint = ZoneMapPin,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documentName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = TextPrimary,
                )
                if (attachedFileName != null) {
                    Text(
                        text = attachedFileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandAccent,
                    )
                } else {
                    TextButton(
                        onClick = onAttachClick,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.documents_attach),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                            ),
                            color = ZoneMapPin,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.documents_types),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.8f),
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.documents_row_cd),
                tint = TextSecondary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentUploadRowPreview() {
    IrayTheme {
        DocumentUploadRow(
            documentName = "Pièce d’identité (CIN ou passeport)",
            attachedFileName = null,
            onAttachClick = {},
        )
    }
}
