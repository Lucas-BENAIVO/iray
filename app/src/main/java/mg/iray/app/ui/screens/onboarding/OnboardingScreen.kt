package mg.iray.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.components.onboarding.OnboardingHero
import mg.iray.app.ui.components.onboarding.OnboardingIntroCard
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage

/**
 * Nouvel écran d’accueil style capture e-Mada, marque Iray.
 *
 * Séparé de [mg.iray.app.ui.screens.welcome.WelcomeScreen] (ancien écran conservé).
 * Hero photo marché + carte intro + CTA "Commencer".
 */
data class OnboardingActions(
    val onStart: () -> Unit = {},
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    actions: OnboardingActions = OnboardingActions(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            OnboardingHero(
                modifier = Modifier.fillMaxWidth(),
            )
            OnboardingIntroCard(
                onStartClick = actions.onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
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
