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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.onboarding.OnboardingHero
import mg.iray.app.ui.components.onboarding.OnboardingIntroCard
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingGreenDeep
import mg.iray.app.ui.theme.OnboardingGreenSoft

/**
 * Onboarding page 1 — esthétique premium (réf. produit), contenu Iray citoyen.
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
            painter = painterResource(R.drawable.onboarding_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            OnboardingGreenDeep.copy(alpha = 0.88f),
                            OnboardingGreenDeep.copy(alpha = 0.72f),
                            OnboardingGreenSoft.copy(alpha = 0.82f),
                            Color(0xFF2F5A42).copy(alpha = 0.9f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 30.dp),
        ) {
            OnboardingHero(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(26.dp))

            OnboardingIntroCard(
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
