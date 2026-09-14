package mg.iray.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextOnBrand

/**
 * CTA principal — pilule FlagGreen + pastille flèche (design system onboarding).
 */
@Composable
fun IrayPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = FlagGreen,
) {
    val contentColor = if (containerColor == BrandWhite) FlagGreen else TextOnBrand
    val arrowBg = if (containerColor == BrandWhite) FlagGreen else BrandWhite
    val arrowTint = if (containerColor == BrandWhite) BrandWhite else FlagGreen

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = containerColor.copy(alpha = 0.28f),
                spotColor = containerColor.copy(alpha = 0.35f),
            )
            .clip(RoundedCornerShape(999.dp))
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.38f))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = BrandWhite),
                onClick = onClick,
            )
            .padding(start = 24.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = IrayFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                letterSpacing = 0.2.sp,
            ),
            color = contentColor.copy(alpha = if (enabled) 1f else 0.7f),
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(arrowBg.copy(alpha = if (enabled) 1f else 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = arrowTint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IrayPrimaryButtonPreview() {
    IrayTheme {
        IrayPrimaryButton(
            label = stringResource(R.string.welcome_hero_cta),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
