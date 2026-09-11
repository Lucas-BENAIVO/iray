package mg.iray.app.ui.components.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.BrandPrimarySoft
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary

/**
 * [Primary] : fond vert très clair + texte sombre.
 * [Quiet] : carte blanche.
 */
enum class WelcomeActionStyle {
    Primary,
    Quiet
}

@Composable
fun WelcomeActionCard(
    title: String,
    icon: ImageVector,
    style: WelcomeActionStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = when (style) {
        WelcomeActionStyle.Primary -> BrandPrimarySoft
        WelcomeActionStyle.Quiet -> BrandWhite
    }
    val content = TextPrimary
    val accent = BrandAccent

    val shape = RoundedCornerShape(12.dp)
    val elevation = if (style == WelcomeActionStyle.Quiet) 5.dp else 3.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = shape, clip = false)
            .clip(shape)
            .background(container)
            .border(
                width = if (style == WelcomeActionStyle.Primary) 1.5.dp else 1.dp,
                color = if (style == WelcomeActionStyle.Primary) {
                    BrandAccent.copy(alpha = 0.35f)
                } else {
                    OutlineOnWhite
                },
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = content,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "»",
            style = MaterialTheme.typography.titleLarge,
            color = accent
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WelcomeActionCardPreview() {
    IrayTheme {
        WelcomeActionCard(
            title = stringResource(R.string.welcome_action_demarches),
            icon = Icons.Outlined.Description,
            style = WelcomeActionStyle.Primary,
            onClick = {}
        )
    }
}
