package mg.iray.app.ui.components.signalement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.irayFastClick
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextPrimary

/**
 * Carte catégorie signalement - même langage carte que les démarches.
 */
@Composable
fun SignalementCategoryRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = FlagRed
    val shape = RoundedCornerShape(22.dp)
    val borderAlpha = if (selected) 0.45f else 0.22f
    val elevation = if (selected) 14.dp else 10.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = OnboardingInk.copy(alpha = 0.14f),
                spotColor = accent.copy(alpha = if (selected) 0.28f else 0.16f),
            )
            .clip(shape)
            .background(
                if (selected) accent.copy(alpha = 0.08f) else BrandWhite,
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = borderAlpha),
                        accent.copy(alpha = 0.06f),
                    ),
                ),
                shape = shape,
            )
            .irayFastClick(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.12f),
                                accent.copy(alpha = 0.20f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(26.dp),
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = (-0.1).sp,
                ),
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.signalement_chevron_cd),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F7F5)
@Composable
private fun SignalementCategoryRowPreview() {
    IrayTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SignalementCategoryRow(
                label = "Routes et voirie",
                icon = Icons.Filled.Eco,
                selected = true,
                onClick = {},
            )
            SignalementCategoryRow(
                label = "Éclairage public",
                icon = Icons.Filled.Eco,
                selected = false,
                onClick = {},
            )
        }
    }
}
