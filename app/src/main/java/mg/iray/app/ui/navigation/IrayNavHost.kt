package mg.iray.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mg.iray.app.ui.screens.onboarding.OnboardingActions
import mg.iray.app.ui.screens.onboarding.OnboardingScreen
import mg.iray.app.ui.screens.profile.ProfileActions
import mg.iray.app.ui.screens.profile.ProfileScreen
import mg.iray.app.ui.screens.welcome.WelcomeActions
import mg.iray.app.ui.screens.welcome.WelcomeScreen

/**
 * Graphe de navigation — best practice : un seul [NavHost], routes
 * centralisées, navigation pilotée par les callbacks des écrans.
 */
object IrayRoute {
    const val ONBOARDING = "onboarding"
    const val PROFILE = "profile"
    const val WELCOME = "welcome"
}

@Composable
fun IrayNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = IrayRoute.ONBOARDING,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(IrayRoute.ONBOARDING) {
            OnboardingScreen(
                actions = OnboardingActions(
                    // "Commencer" → page profil.
                    onStart = { navController.navigate(IrayRoute.PROFILE) },
                ),
            )
        }
        composable(IrayRoute.PROFILE) {
            ProfileScreen(
                actions = ProfileActions(
                    onBack = { navController.popBackStack() },
                    onAvatarClick = { /* TODO: sélecteur photo */ },
                    onContinue = { navController.navigate(IrayRoute.WELCOME) },
                ),
            )
        }
        composable(IrayRoute.WELCOME) {
            WelcomeScreen(
                actions = WelcomeActions(
                    onDemarches = { /* TODO: navigate */ },
                    onSignalements = { /* TODO: navigate */ },
                    onAskQuestion = { /* TODO: navigate */ },
                    onFeaturedCta = { /* TODO: navigate */ },
                ),
            )
        }
    }
}
