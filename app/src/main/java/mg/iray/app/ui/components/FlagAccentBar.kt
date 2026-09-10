package mg.iray.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.BrandGreen
import mg.iray.app.ui.theme.BrandRed
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite

/**
 * Fine barre tricolore (Blanc · Rouge · Vert) — rappel discret du drapeau.
 */
@Composable
fun FlagAccentBar(
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .border(0.5.dp, OutlineOnWhite.copy(alpha = 0.6f), shape)
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BrandWhite)
        )
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BrandRed)
        )
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BrandGreen)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlagAccentBarPreview() {
    IrayTheme {
        FlagAccentBar(modifier = Modifier.fillMaxWidth())
    }
}
