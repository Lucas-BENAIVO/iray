package mg.iray.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.onboarding.OnboardingBottomContent
import mg.iray.app.ui.components.onboarding.OnboardingTopBrand
import mg.iray.app.ui.theme.IrayTheme

/**
 * Onboarding page 1 — style immersif photo pleine page
 * (réf. carpool : fond réel, texte bas, UI minimale).
 */
data class OnboardingActions(
    val onStart: () -> Unit = {},
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    actions: OnboardingActions = OnboardingActions(),
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.onboarding_hero),
            contentDescription = stringResource(R.string.welcome_content_desc_hero),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Voile cinématique — lisibilité du texte blanc (style référence).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.28f),
                            0.35f to Color.Black.copy(alpha = 0.12f),
                            0.62f to Color.Black.copy(alpha = 0.35f),
                            0.82f to Color.Black.copy(alpha = 0.62f),
                            1.0f to Color.Black.copy(alpha = 0.84f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
                .padding(top = 20.dp, bottom = 28.dp),
        ) {
            OnboardingTopBrand(
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            OnboardingBottomContent(
                onStartClick = actions.onStart,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun OnboardingScreenPreview() {
    IrayTheme {
        OnboardingScreen()
    }
}
