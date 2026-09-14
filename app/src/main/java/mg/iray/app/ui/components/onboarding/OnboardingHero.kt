package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingMutedOnGreen

/**
 * En-tête onboarding — marque Iray, barre drapeau, promesse typographique.
 */
@Composable
fun OnboardingHero(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 28.dp)
            .padding(top = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.welcome_hero_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            ),
            color = BrandWhite.copy(alpha = 0.95f),
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlagAccentBar(
            modifier = Modifier.fillMaxWidth(0.22f),
            height = 3.dp,
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = stringResource(R.string.welcome_hero_line_strong_1),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = BrandWhite,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.welcome_hero_line_muted),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = OnboardingMutedOnGreen,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.welcome_hero_line_strong_2),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.4).sp,
            ),
            color = BrandWhite,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF16382C)
@Composable
private fun OnboardingHeroPreview() {
    IrayTheme {
        OnboardingHero()
    }
}
