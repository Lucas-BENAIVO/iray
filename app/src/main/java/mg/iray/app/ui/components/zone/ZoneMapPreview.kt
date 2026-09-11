package mg.iray.app.ui.components.zone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.ZoneMapBackground
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Aperçu carte de la zone — placeholder en attendant le SDK carto.
 *
 * Fond beige façon carte + punaise bleue centrée, coins arrondis.
 */
@Composable
fun ZoneMapPreview(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        color = ZoneMapBackground,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = stringResource(R.string.zone_map_cd),
                tint = ZoneMapPin,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZoneMapPreviewPreview() {
    IrayTheme {
        ZoneMapPreview()
    }
}
