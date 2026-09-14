package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Carte centrale photo réelle + CTA + barre Commencer.
 */
@Composable
fun OnboardingIntroCard(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(44.dp),
                        ambientColor = OnboardingInk.copy(alpha = 0.3f),
                        spotColor = OnboardingInk.copy(alpha = 0.22f),
                    ),
                shape = RoundedCornerShape(44.dp),
                color = BrandWhite,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 38.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.onboarding_hero),
                        contentDescription = stringResource(R.string.welcome_content_desc_hero),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(32.dp)),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.welcome_card_title),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        color = TextSecondary,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.welcome_card_subtitle),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            letterSpacing = (-0.2).sp,
                        ),
                        color = TextPrimary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = 22.dp)
                    .size(64.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(OnboardingInk)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = BrandWhite),
                        onClick = onStartClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.welcome_cta_start_cd),
                    tint = BrandWhite,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(38.dp))

        OnboardingStartBar(
            onStartClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
        )
    }
}

@Composable
private fun OnboardingStartBar(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .shadow(18.dp, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .background(OnboardingInk)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = BrandWhite),
                onClick = onStartClick,
            )
            .padding(start = 24.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.welcome_hero_cta),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = BrandWhite,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DiscoverThumb(
                drawableRes = R.drawable.onboarding_thumb_1,
                contentDescription = stringResource(R.string.welcome_content_desc_thumb_1),
            )
            DiscoverThumb(
                drawableRes = R.drawable.onboarding_thumb_2,
                contentDescription = stringResource(R.string.welcome_content_desc_thumb_2),
            )
            DiscoverThumb(
                drawableRes = R.drawable.onboarding_thumb_3,
                contentDescription = stringResource(R.string.welcome_content_desc_thumb_3),
            )
        }
    }
}

@Composable
private fun DiscoverThumb(
    drawableRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp)),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF16382C, heightDp = 780)
@Composable
private fun OnboardingIntroCardPreview() {
    IrayTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            OnboardingIntroCard(onStartClick = {})
        }
    }
}
