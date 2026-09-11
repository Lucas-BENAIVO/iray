package mg.iray.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailActions
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailScreen
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailsCatalog
import mg.iray.app.ui.screens.onboarding.OnboardingActions
import mg.iray.app.ui.screens.onboarding.OnboardingScreen
import mg.iray.app.ui.screens.profile.ProfileActions
import mg.iray.app.ui.screens.profile.ProfileScreen
import mg.iray.app.ui.screens.recap.RecapActions
import mg.iray.app.ui.screens.recap.RecapScreen
import mg.iray.app.ui.screens.demarches.DemarchesActions
import mg.iray.app.ui.screens.demarches.DemarchesScreen
import mg.iray.app.ui.screens.confirmation.ConfirmationActions
import mg.iray.app.ui.screens.confirmation.ConfirmationScreen
import mg.iray.app.ui.screens.documents.DocumentsActions
import mg.iray.app.ui.screens.documents.DocumentsScreen
import mg.iray.app.ui.screens.form.FormActions
import mg.iray.app.ui.screens.form.FormScreen
import mg.iray.app.ui.screens.notifications.NotificationsActions
import mg.iray.app.ui.screens.notifications.NotificationsScreen
import mg.iray.app.ui.screens.success.SuccessActions
import mg.iray.app.ui.screens.success.SuccessScreen
import mg.iray.app.ui.screens.upload.UploadActions
import mg.iray.app.ui.screens.upload.UploadScreen
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
    const val NOTIFICATIONS = "notifications"
    const val DEMARCHES = "demarches"
    const val DEMARCHE_DETAIL = "demarche_detail/{demarcheId}"
    const val DOCUMENTS = "documents/{demarcheId}"
    const val UPLOAD = "upload/{demarcheId}/{docIndex}"
    const val FORM = "form/{demarcheId}"
    const val RECAP = "recap/{demarcheId}"
    const val CONFIRMATION = "confirmation/{demarcheId}"
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
    var birthdate by rememberSaveable { mutableStateOf("") }
    var commune by rememberSaveable { mutableStateOf("") }
    var fokontany by rememberSaveable { mutableStateOf("") }
    // Fichiers joints par "demarcheId/index" — partagés documents ↔ upload.
    val attachedByKey = remember { mutableStateMapOf<String, String>() }

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
                        birthdate = form.birthdate
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
                userFullName = "$firstName $lastName".trim(),
                userZoneLabel = "$commune, $fokontany",
                actions = WelcomeActions(
                    onDemarches = { navController.navigate(IrayRoute.DEMARCHES) },
                    onSignalements = { /* TODO: navigate */ },
                    onNotifications = { navController.navigate(IrayRoute.NOTIFICATIONS) },
                    onFeaturedCta = { /* TODO: navigate */ },
                ),
            )
        }
        composable(IrayRoute.DEMARCHES) {
            DemarchesScreen(
                actions = DemarchesActions(
                    onBack = { navController.popBackStack() },
                    onDemarcheClick = { demarcheId ->
                        navController.navigate("demarche_detail/$demarcheId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.DEMARCHE_DETAIL,
            arguments = listOf(navArgument("demarcheId") { type = NavType.StringType }),
        ) { backStackEntry ->
            DemarcheDetailScreen(
                demarcheId = backStackEntry.arguments?.getString("demarcheId").orEmpty(),
                actions = DemarcheDetailActions(
                    onBack = { navController.popBackStack() },
                    onStartRequest = { demarcheId ->
                        navController.navigate("form/$demarcheId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.DOCUMENTS,
            arguments = listOf(navArgument("demarcheId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val docDemarcheId =
                backStackEntry.arguments?.getString("demarcheId").orEmpty()
            val prefix = "$docDemarcheId/"
            DocumentsScreen(
                demarcheId = docDemarcheId,
                externalAttached = attachedByKey
                    .filterKeys { it.startsWith(prefix) }
                    .mapKeys { it.key.removePrefix(prefix).toIntOrNull() ?: -1 }
                    .filterKeys { it >= 0 },
                actions = DocumentsActions(
                    onBack = { navController.popBackStack() },
                    onAttachFile = { index ->
                        navController.navigate("upload/$docDemarcheId/$index")
                    },
                    onNext = {
                        navController.navigate("recap/$docDemarcheId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.RECAP,
            arguments = listOf(navArgument("demarcheId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val recapDemarcheId =
                backStackEntry.arguments?.getString("demarcheId").orEmpty()
            val docCount = DemarcheDetailsCatalog
                .get(recapDemarcheId)
                .documents
                .size
            val prefix = "$recapDemarcheId/"
            RecapScreen(
                demarcheId = recapDemarcheId,
                attachedFileNames = (0 until docCount).mapNotNull { index ->
                    attachedByKey["$prefix$index"]
                },
                actions = RecapActions(
                    onBack = { navController.popBackStack() },
                    onSubmit = { demarcheId ->
                        navController.navigate("confirmation/$demarcheId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.CONFIRMATION,
            arguments = listOf(navArgument("demarcheId") { type = NavType.StringType }),
        ) { backStackEntry ->
            ConfirmationScreen(
                demarcheId = backStackEntry.arguments?.getString("demarcheId").orEmpty(),
                actions = ConfirmationActions(
                    onBack = { navController.popBackStack() },
                    // "Voir mes demandes" → accueil (page Mes demandes : TODO).
                    onViewRequests = { navController.navigate(IrayRoute.WELCOME) },
                ),
            )
        }
        composable(
            route = IrayRoute.FORM,
            arguments = listOf(navArgument("demarcheId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val formDemarcheId =
                backStackEntry.arguments?.getString("demarcheId").orEmpty()
            FormScreen(
                initialLastName = lastName,
                initialFirstName = firstName,
                initialBirthdate = birthdate,
                initialAddress = fokontany,
                asksBirthPlace = DemarcheDetailsCatalog
                    .get(formDemarcheId)
                    .asksBirthPlace,
                actions = FormActions(
                    onBack = { navController.popBackStack() },
                    onSubmit = {
                        navController.navigate("documents/$formDemarcheId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.UPLOAD,
            arguments = listOf(
                navArgument("demarcheId") { type = NavType.StringType },
                navArgument("docIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val upDemarcheId =
                backStackEntry.arguments?.getString("demarcheId").orEmpty()
            val docIndex =
                backStackEntry.arguments?.getInt("docIndex") ?: 0
            val documentName =
                DemarcheDetailsCatalog
                    .get(upDemarcheId)
                    .documents
                    .getOrElse(docIndex) { "" }
            UploadScreen(
                documentName = documentName,
                actions = UploadActions(
                    onBack = { navController.popBackStack() },
                    onFileConfirmed = { fileName ->
                        attachedByKey["$upDemarcheId/$docIndex"] = fileName
                        navController.popBackStack()
                    },
                ),
            )
        }
        composable(IrayRoute.NOTIFICATIONS) {
            NotificationsScreen(
                actions = NotificationsActions(
                    onBack = { navController.popBackStack() },
                    onFilterClick = { /* TODO: filtre avancé */ },
                    onNotificationClick = { /* TODO: détail */ },
                ),
            )
        }
    }
}
