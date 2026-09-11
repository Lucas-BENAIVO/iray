package mg.iray.app.ui.components.signalement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Ligne catégorie de signalement — pastille icône + libellé + chevron.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun SignalementCategoryRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) ZoneMapPin.copy(alpha = 0.10f) else BrandWhite,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) ZoneMapPin else OutlineOnWhite,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = ZoneMapPin.copy(alpha = 0.14f),
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ZoneMapPin,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.signalement_chevron_cd),
                tint = BrandAccent,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignalementCategoryRowPreview() {
    IrayTheme {
        SignalementCategoryRow(
            label = "Routes et voirie",
            icon = Icons.Filled.Eco,
            selected = true,
            onClick = {},
        )
    }
}
