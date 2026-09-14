package mg.iray.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite

/**
 * CTA secondaire — pilule contour, design system onboarding.
 */
@Composable
fun IraySecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, OutlineOnWhite),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = FlagGreen,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = IrayFontFamily,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IraySecondaryButtonPreview() {
    IrayTheme {
        IraySecondaryButton(label = "Retour à l’accueil", onClick = {})
    }
}
