package mg.iray.app.ui.components.confirmation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.FlagGreen

/**
 * Carte N° de dossier - icône doc + libellé + numéro.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun ConfirmationDossierCard(
    dossierNumber: String,
    modifier: Modifier = Modifier,
    accentColor: Color = FlagGreen,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceFeatured,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.14f),
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.confirmation_dossier_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text = dossierNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TextPrimary,
                )
            }
        }
    }
}

/**
 * Timeline de suivi - pastille pleine (étape en cours) + creuses (à venir).
 *
 * Best practice : stateless, étapes en paramètres.
 */
@Composable
fun ConfirmationTimeline(
    currentStepIndex: Int,
    steps: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val reached = index <= currentStepIndex
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = if (reached) FlagGreen
                        else Color.Transparent,
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (reached) FlagGreen else OutlineOnWhite,
                        ),
                        modifier = Modifier.size(16.dp),
                    ) {}
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(22.dp)
                                .background(OutlineOnWhite),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (index == currentStepIndex) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },
                    ),
                    color = if (index <= currentStepIndex) TextPrimary else TextSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationBlocksPreview() {
    IrayTheme {
        Column {
            ConfirmationDossierCard(dossierNumber = "MAD-2026-004821")
        }
    }
}
