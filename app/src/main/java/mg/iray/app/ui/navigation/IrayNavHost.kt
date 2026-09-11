package mg.iray.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mg.iray.app.ui.screens.onboarding.OnboardingActions
import mg.iray.app.ui.screens.onboarding.OnboardingScreen
import mg.iray.app.ui.screens.profile.ProfileActions
import mg.iray.app.ui.screens.profile.ProfileScreen
import mg.iray.app.ui.screens.success.SuccessActions
import mg.iray.app.ui.screens.success.SuccessScreen
import mg.iray.app.ui.screens.welcome.WelcomeActions
import mg.iray.app.ui.screens.welcome.WelcomeScreen
import mg.iray.app.ui.screens.zone.ZoneActions
import mg.iray.app.ui.screens.zone.ZoneScreen

/**
 * Graphe de navigation — best practice : un seul [NavHost], routes
 * centralisées, navigation pilotée par les callbacks des écrans.
 */
object IrayRoute {
    const val ONBOARDING = "onboarding"
    const val PROFILE = "profile"
    const val ZONE = "zone"
    const val SUCCESS = "success"
    const val WELCOME = "welcome"
}

@Composable
fun IrayNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = IrayRoute.ONBOARDING,
) {
    val navController = rememberNavController()

    // Données du parcours conservées pour l’écran succès (survit à la rotation).
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var commune by rememberSaveable { mutableStateOf("") }
    var fokontany by rememberSaveable { mutableStateOf("") }

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
                    onContinue = { form ->
                        firstName = form.firstName
                        lastName = form.lastName
                        navController.navigate(IrayRoute.ZONE)
                    },
                ),
            )
        }
        composable(IrayRoute.ZONE) {
            ZoneScreen(
                actions = ZoneActions(
                    onContinue = { selection ->
                        commune = selection.commune
                        fokontany = selection.fokontany
                        navController.navigate(IrayRoute.SUCCESS)
                    },
                ),
            )
        }
        composable(IrayRoute.SUCCESS) {
            SuccessScreen(
                fullName = "$firstName $lastName".trim(),
                zoneLabel = "$commune, $fokontany",
                actions = SuccessActions(
                    // "C'est parti !" → accueil.
                    onStart = { navController.navigate(IrayRoute.WELCOME) },
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
