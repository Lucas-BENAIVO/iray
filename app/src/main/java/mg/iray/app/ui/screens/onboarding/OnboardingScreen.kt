package mg.iray.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
 * Onboarding immersif — photo pleine page + overlay cinématique + contenu bas.
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
            alignment = androidx.compose.ui.Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        )

        // Précharge la bannière Welcome pendant l’onboarding → ouverture immédiate.
        Image(
            painter = painterResource(R.drawable.welcome_banner),
            contentDescription = null,
            modifier = Modifier
                .size(1.dp)
                .alpha(0f),
        )

        // Overlay chaud / profond — laisse respirer le ciel, ancre le texte en bas.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFF0B1A14).copy(alpha = 0.48f),
                            0.20f to Color(0xFF0B1A14).copy(alpha = 0.12f),
                            0.42f to Color.Transparent,
                            0.62f to Color(0xFF0E1C16).copy(alpha = 0.45f),
                            0.80f to Color(0xFF07140F).copy(alpha = 0.78f),
                            1.00f to Color(0xFF050E0A).copy(alpha = 0.93f),
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
                .padding(top = 18.dp, bottom = 24.dp),
        ) {
            OnboardingTopBrand(modifier = Modifier.fillMaxWidth())

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
