package mg.iray.app.ui.components.signalement

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.screens.signalement.SignalementPriority
import mg.iray.app.ui.theme.BrandDanger
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.PriorityOrange
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Carte récap du signalement — catégorie + localisation + description + priorité.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun SignalementRecapCard(
    categoryLabel: String,
    categoryIcon: ImageVector,
    subcategory: String,
    address: String,
    description: String,
    priority: SignalementPriority,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceFeatured,
        border = BorderStroke(1.dp, OutlineOnWhite.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = ZoneMapPin.copy(alpha = 0.14f),
                    modifier = Modifier.size(44.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = ZoneMapPin,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = TextPrimary,
                    )
                    Text(
                        text = subcategory,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LabeledText(
                label = stringResource(R.string.signalement_location_row_label),
                value = address,
            )
            Spacer(modifier = Modifier.height(6.dp))
            LabeledText(
                label = stringResource(R.string.signalement_description_row_label),
                value = description,
            )

            Spacer(modifier = Modifier.height(12.dp))

            PriorityPill(priority = priority)
        }
    }
}

@Composable
private fun LabeledText(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = TextSecondary)) { append(label) }
            withStyle(
                SpanStyle(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                ),
            ) { append(value) }
        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun PriorityPill(
    priority: SignalementPriority,
    modifier: Modifier = Modifier,
) {
    val (labelRes, color) = when (priority) {
        SignalementPriority.HIGH ->
            R.string.signalement_priority_high to BrandDanger
        SignalementPriority.MEDIUM ->
            R.string.signalement_priority_medium to PriorityOrange
        SignalementPriority.LOW ->
            R.string.signalement_priority_low to FlagGreen
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.14f),
        modifier = modifier,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextSecondary)) {
                    append(stringResource(R.string.signalement_priority_label))
                }
                withStyle(
                    SpanStyle(color = color, fontWeight = FontWeight.SemiBold),
                ) { append(stringResource(labelRes)) }
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignalementRecapCardPreview() {
    IrayTheme {
        SignalementRecapCard(
            categoryLabel = "Électricité",
            categoryIcon = Icons.Filled.Bolt,
            subcategory = "Coupure d’électricité",
            address = "Fokontany Andohalo, Antananarivo",
            description = "Coupure depuis ce matin",
            priority = SignalementPriority.HIGH,
        )
    }
}
