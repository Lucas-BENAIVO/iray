package mg.iray.app.ui.components.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextPrimary

@Composable
fun OnboardingBottomContent(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        // Accent discret — petit trait vert (pas d’icône « virus »).
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FlagGreen),
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_headline_line1),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = IrayDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.4.sp,
            ),
            color = BrandWhite,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_headline_line2),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = IrayDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.4.sp,
            ),
            color = BrandWhite,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = IrayFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
            color = BrandWhite.copy(alpha = 0.84f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        OnboardingPrimaryCta(
            label = stringResource(R.string.welcome_hero_cta),
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        FlagAccentBar(
            modifier = Modifier.fillMaxWidth(),
            height = 4.dp,
        )
    }
}

/**
 * CTA premium sur fond photo — pilule blanche, typo nette, flèche en pastille.
 * Micro-effet au clic (échelle + légère opacité) avant navigation.
 */
@Composable
private fun OnboardingPrimaryCta(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tapped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scale by animateFloatAsState(
        targetValue = if (tapped) 0.96f else 1f,
        animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
        label = "ctaScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (tapped) 0.86f else 1f,
        animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
        label = "ctaAlpha",
    )

    Row(
        modifier = modifier
            .height(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = OnboardingInk.copy(alpha = 0.35f),
                spotColor = OnboardingInk.copy(alpha = 0.28f),
            )
            .clip(RoundedCornerShape(999.dp))
            .background(BrandWhite)
            .clickable(
                enabled = !tapped,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = {
                    tapped = true
                    scope.launch {
                        delay(120)
                        onClick()
                    }
                },
            )
            .padding(start = 26.dp, end = 8.dp),
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
            color = TextPrimary,
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(FlagGreen),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = BrandWhite,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A1510)
@Composable
private fun OnboardingBottomContentPreview() {
    IrayTheme {
        OnboardingBottomContent(
            onStartClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )
    }
}
