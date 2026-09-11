package mg.iray.app.ui.components.demarches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Filtre de démarches — pastille bleue si actif, blanche sinon.
 *
 * Best practice : stateless ([selected]/[onClick] hoistés).
 */
@Composable
fun DemarcheFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) ZoneMapPin else BrandWhite,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) ZoneMapPin else OutlineOnWhite,
        ),
        modifier = modifier.heightIn(min = 36.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (selected) BrandWhite else TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DemarcheFilterChipPreview() {
    IrayTheme {
        DemarcheFilterChip(label = "Tout", selected = true, onClick = {})
    }
}
