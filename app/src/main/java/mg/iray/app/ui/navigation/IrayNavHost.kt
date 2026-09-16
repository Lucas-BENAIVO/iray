package mg.iray.app.ui.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mg.iray.app.ui.controller.ProfileController
import mg.iray.app.ui.controller.RequestController
import mg.iray.app.ui.mapper.toMesDemarcheItem
import mg.iray.app.ui.mapper.toMesSignalementItem
import mg.iray.app.ui.theme.SurfacePage
import androidx.compose.foundation.background
import mg.iray.app.ui.screens.confirmation.ConfirmationActions
import mg.iray.app.ui.screens.confirmation.ConfirmationScreen
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailActions
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailScreen
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailsCatalog
import mg.iray.app.ui.screens.demarches.DemarchesActions
import mg.iray.app.ui.screens.demarches.DemarchesScreen
import mg.iray.app.ui.screens.documents.DocumentsActions
import mg.iray.app.ui.screens.documents.DocumentsScreen
import mg.iray.app.ui.screens.form.FormActions
import mg.iray.app.ui.screens.form.FormScreen
import mg.iray.app.ui.screens.mesDemarches.MesDemarchesActions
import mg.iray.app.ui.screens.mesDemarches.MesDemarchesScreen
import mg.iray.app.ui.screens.mesSignalements.MesSignalementsActions
import mg.iray.app.ui.screens.mesSignalements.MesSignalementsScreen
import mg.iray.app.ui.screens.monProfil.MonProfilActions
import mg.iray.app.ui.screens.monProfil.MonProfilScreen
import mg.iray.app.ui.screens.notifications.NotificationsActions
import mg.iray.app.ui.screens.notifications.NotificationsScreen
import mg.iray.app.ui.screens.onboarding.OnboardingActions
import mg.iray.app.ui.screens.onboarding.OnboardingScreen
import mg.iray.app.ui.screens.profile.ProfileActions
import mg.iray.app.ui.screens.profile.ProfileScreen
import mg.iray.app.ui.screens.recap.RecapActions
import mg.iray.app.ui.screens.recap.RecapScreen
import mg.iray.app.ui.screens.signalement.SignalementCategoryActions
import mg.iray.app.ui.screens.signalement.SignalementCategoryScreen
import mg.iray.app.ui.screens.signalement.SignalementConfirmActions
import mg.iray.app.ui.screens.signalement.SignalementConfirmScreen
import mg.iray.app.ui.screens.signalement.SignalementDetailsActions
import mg.iray.app.ui.screens.signalement.SignalementDetailsScreen
import mg.iray.app.ui.screens.signalement.SignalementLocationActions
import mg.iray.app.ui.screens.signalement.SignalementLocationScreen
import mg.iray.app.ui.screens.signalement.SignalementSubcategoryActions
import mg.iray.app.ui.screens.signalement.SignalementSubcategoryScreen
import mg.iray.app.ui.screens.signalement.SignalementSuccessActions
import mg.iray.app.ui.screens.signalement.SignalementSuccessScreen
import mg.iray.app.ui.screens.success.SuccessActions
import mg.iray.app.ui.screens.success.SuccessScreen
import mg.iray.app.ui.screens.upload.UploadActions
import mg.iray.app.ui.screens.upload.UploadScreen
import mg.iray.app.ui.screens.welcome.WelcomeActions
import mg.iray.app.ui.screens.welcome.WelcomeScreen
import mg.iray.app.ui.screens.zone.ZoneActions
import mg.iray.app.ui.screens.zone.ZoneScreen

/**
 * Graphe de navigation - best practice : un seul [NavHost], routes
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
    const val SIGNALEMENT = "signalement"
    const val SIGNALEMENT_DETAIL = "signalement_detail/{categoryId}"
    const val SIGNALEMENT_LOCATION = "signalement_location/{categoryId}"
    const val SIGNALEMENT_DETAILS = "signalement_details/{categoryId}/{subcategory}"
    const val SIGNALEMENT_CONFIRM = "signalement_confirm"
    const val SIGNALEMENT_SUCCESS = "signalement_success"
    const val MES_DEMARCHES = "mes_demarches"
    const val MES_SIGNALEMENTS = "mes_signalements"
    const val MON_PROFIL = "mon_profil"
    const val WELCOME = "welcome"
}

/** Onglet Profil : fiche si déjà créé, sinon parcours de création. */
private fun NavHostController.openProfileTab(hasProfile: Boolean) {
    navigate(if (hasProfile) IrayRoute.MON_PROFIL else IrayRoute.PROFILE) {
        launchSingleTop = true
    }
}

@Composable
fun IrayNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val profileController: ProfileController = viewModel(factory = ProfileController.Factory)
    val requestController: RequestController = viewModel(factory = RequestController.Factory)
    val profile by profileController.profile.collectAsStateWithLifecycle()
    val hasProfileRemote by profileController.hasProfile.collectAsStateWithLifecycle()
    val profileReady by profileController.ready.collectAsStateWithLifecycle()
    val requests by requestController.requests.collectAsStateWithLifecycle()
    val signalements by requestController.signalements.collectAsStateWithLifecycle()
    val lastRequestRef by requestController.lastRequestRef.collectAsStateWithLifecycle()
    val lastSignalementRef by requestController.lastSignalementRef.collectAsStateWithLifecycle()

    // Données du parcours signalement (survivent à la rotation).
    var sigCategoryId by rememberSaveable { mutableStateOf("") }
    var sigSubcategory by rememberSaveable { mutableStateOf("") }
    var sigAddress by rememberSaveable { mutableStateOf("") }
    var sigLatitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var sigLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var sigDescription by rememberSaveable { mutableStateOf("") }
    var sigPhotos by rememberSaveable { mutableStateOf(listOf<String>()) }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var birthdate by rememberSaveable { mutableStateOf("") }
    var commune by rememberSaveable { mutableStateOf("") }
    var fokontany by rememberSaveable { mutableStateOf("") }
    var hasProfile by rememberSaveable { mutableStateOf(false) }
    // Fichiers joints par "demarcheId/index" - partagés documents ↔ upload.
    val attachedByKey = remember { mutableStateMapOf<String, String>() }

    val openProfile = hasProfileRemote || hasProfile

    // Hydrate l’UI depuis Room. Profil « complet » = identité + faritra
    // (sinon Faritrao flip startDestination → Welcome au milieu du parcours).
    LaunchedEffect(profile) {
        val p = profile ?: return@LaunchedEffect
        if (p.firstName.isNotBlank()) firstName = p.firstName
        if (p.lastName.isNotBlank()) lastName = p.lastName
        if (p.phone.isNotBlank()) phone = p.phone
        if (p.birthdate.isNotBlank()) birthdate = p.birthdate
        if (p.commune.isNotBlank()) commune = p.commune
        if (p.fokontany.isNotBlank()) fokontany = p.fokontany
        if (
            p.firstName.isNotBlank() &&
            p.lastName.isNotBlank() &&
            p.commune.isNotBlank() &&
            p.fokontany.isNotBlank()
        ) {
            hasProfile = true
        }
    }

    // Attendre la 1ʳᵉ lecture profil : évite de rejouer l’onboarding si déjà citoyen.
    if (!profileReady) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SurfacePage),
        )
        return
    }

    // Figé au 1er rendu NavHost : ne jamais changer startDestination en cours de session
    // (sinon recréation du graphe → redirection forcée vers Welcome).
    val startDestination = remember {
        if (hasProfileRemote || hasProfile) IrayRoute.WELCOME else IrayRoute.ONBOARDING
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(IrayRoute.ONBOARDING) {
            OnboardingScreen(
                actions = OnboardingActions(
                    // "Commencer" → accueil (profil se fait depuis l’onglet Profil).
                    onStart = {
                        navController.navigate(IrayRoute.WELCOME) {
                            popUpTo(IrayRoute.ONBOARDING) { inclusive = true }
                        }
                    },
                ),
            )
        }
        composable(IrayRoute.PROFILE) {
            ProfileScreen(
                initialFirstName = profile?.firstName?.takeIf { it.isNotBlank() } ?: firstName,
                initialLastName = profile?.lastName?.takeIf { it.isNotBlank() } ?: lastName,
                initialPhone = profile?.phone?.takeIf { it.isNotBlank() } ?: phone,
                initialBirthdate = profile?.birthdate?.takeIf { it.isNotBlank() } ?: birthdate,
                actions = ProfileActions(
                    onBack = { navController.popBackStack() },
                    onAvatarClick = { /* TODO: sélecteur photo */ },
                    onContinue = { form ->
                        firstName = form.firstName
                        lastName = form.lastName
                        phone = form.phone
                        birthdate = form.birthdate
                        profileController.saveIdentity(
                            firstName = form.firstName,
                            lastName = form.lastName,
                            phone = form.phone,
                            birthdate = form.birthdate,
                        )
                        if (hasProfile) {
                            navController.navigate(IrayRoute.MON_PROFIL) {
                                popUpTo(IrayRoute.MON_PROFIL) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(IrayRoute.ZONE)
                        }
                    },
                ),
            )
        }
        composable(IrayRoute.ZONE) {
            ZoneScreen(
                actions = ZoneActions(
                    // Toujours un retour : création → profil identité, édition → mon profil.
                    onBack = { navController.popBackStack() },
                    onContinue = { selection ->
                        commune = selection.commune
                        fokontany = selection.fokontany
                        profileController.saveZone(
                            commune = selection.commune,
                            fokontany = selection.fokontany,
                        )
                        if (hasProfile) {
                            // Édition zone depuis Mon profil - retour direct.
                            navController.navigate(IrayRoute.MON_PROFIL) {
                                popUpTo(IrayRoute.MON_PROFIL) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(IrayRoute.SUCCESS)
                        }
                    },
                ),
            )
        }
        composable(IrayRoute.SUCCESS) {
            SuccessScreen(
                fullName = "$firstName $lastName".trim(),
                zoneLabel = "$commune, $fokontany",
                actions = SuccessActions(
                    onStart = {
                        hasProfile = true
                        navController.navigate(IrayRoute.WELCOME) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                ),
            )
        }
        composable(IrayRoute.WELCOME) {
            WelcomeScreen(
                actions = WelcomeActions(
                    // Cartes d’accueil = démarrer une nouvelle demande / signalement.
                    onDemarches = { navController.navigate(IrayRoute.DEMARCHES) },
                    onSignalements = { navController.navigate(IrayRoute.SIGNALEMENT) },
                    // Bottom bar = historiques "Mes …".
                    onMesDemarches = { navController.navigate(IrayRoute.MES_DEMARCHES) },
                    onMesSignalements = {
                        navController.navigate(IrayRoute.MES_SIGNALEMENTS)
                    },
                    onNotifications = { navController.navigate(IrayRoute.NOTIFICATIONS) },
                    onProfile = { navController.openProfileTab(openProfile) },
                ),
            )
        }
        composable(IrayRoute.MON_PROFIL) {
            MonProfilScreen(
                firstName = profile?.firstName?.takeIf { it.isNotBlank() } ?: firstName,
                lastName = profile?.lastName?.takeIf { it.isNotBlank() } ?: lastName,
                phone = profile?.phone?.takeIf { it.isNotBlank() } ?: phone,
                birthdate = profile?.birthdate?.takeIf { it.isNotBlank() } ?: birthdate,
                commune = profile?.commune?.takeIf { it.isNotBlank() } ?: commune,
                fokontany = profile?.fokontany?.takeIf { it.isNotBlank() } ?: fokontany,
                actions = MonProfilActions(
                    onBack = { navController.popBackStack() },
                    onHome = {
                        navController.navigate(IrayRoute.WELCOME) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onDemarches = {
                        navController.navigate(IrayRoute.MES_DEMARCHES) {
                            launchSingleTop = true
                        }
                    },
                    onSignalements = {
                        navController.navigate(IrayRoute.MES_SIGNALEMENTS) {
                            launchSingleTop = true
                        }
                    },
                    onEditProfile = { navController.navigate(IrayRoute.PROFILE) },
                    onEditZone = { navController.navigate(IrayRoute.ZONE) },
                ),
            )
        }
        composable(IrayRoute.MES_DEMARCHES) {
            MesDemarchesScreen(
                items = requests.map { it.toMesDemarcheItem() },
                actions = MesDemarchesActions(
                    onBack = { navController.popBackStack() },
                    onHome = {
                        navController.navigate(IrayRoute.WELCOME) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onSignalements = {
                        navController.navigate(IrayRoute.MES_SIGNALEMENTS) {
                            launchSingleTop = true
                        }
                    },
                    onProfile = { navController.openProfileTab(openProfile) },
                    onNewRequest = { navController.navigate(IrayRoute.DEMARCHES) },
                    onRequestClick = { /* TODO: détail suivi */ },
                ),
            )
        }
        composable(IrayRoute.MES_SIGNALEMENTS) {
            MesSignalementsScreen(
                items = signalements.map { it.toMesSignalementItem() },
                actions = MesSignalementsActions(
                    onBack = { navController.popBackStack() },
                    onHome = {
                        navController.navigate(IrayRoute.WELCOME) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onDemarches = {
                        navController.navigate(IrayRoute.MES_DEMARCHES) {
                            launchSingleTop = true
                        }
                    },
                    onProfile = { navController.openProfileTab(openProfile) },
                    onNewReport = { navController.navigate(IrayRoute.SIGNALEMENT) },
                    onReportClick = { /* TODO: détail suivi */ },
                ),
            )
        }
        composable(IrayRoute.SIGNALEMENT) {
            SignalementCategoryScreen(
                actions = SignalementCategoryActions(
                    onBack = { navController.popBackStack() },
                    onCategoryClick = { categoryId ->
                        navController.navigate("signalement_detail/$categoryId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.SIGNALEMENT_DETAIL,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
        ) { backStackEntry ->
            SignalementSubcategoryScreen(
                categoryId = backStackEntry.arguments?.getString("categoryId").orEmpty(),
                actions = SignalementSubcategoryActions(
                    onBack = { navController.popBackStack() },
                    onContinue = { categoryId, subcategory ->
                        sigCategoryId = categoryId
                        sigSubcategory = subcategory
                        navController.navigate("signalement_location/$categoryId")
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.SIGNALEMENT_LOCATION,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val locCategoryId =
                backStackEntry.arguments?.getString("categoryId").orEmpty()
            val zoneFokontany = profile?.fokontany?.takeIf { it.isNotBlank() } ?: fokontany
            val zoneCommune = profile?.commune?.takeIf { it.isNotBlank() } ?: commune
            SignalementLocationScreen(
                initialAddress = "Fokontany $zoneFokontany, $zoneCommune",
                initialLatitude = sigLatitude,
                initialLongitude = sigLongitude,
                actions = SignalementLocationActions(
                    onBack = { navController.popBackStack() },
                    onContinue = { address, lat, lng ->
                        sigAddress = address
                        sigLatitude = lat
                        sigLongitude = lng
                        navController.navigate(
                            "signalement_details/$locCategoryId/" +
                                Uri.encode(sigSubcategory),
                        )
                    },
                ),
            )
        }
        composable(
            route = IrayRoute.SIGNALEMENT_DETAILS,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("subcategory") { type = NavType.StringType },
            ),
        ) {
            SignalementDetailsScreen(
                actions = SignalementDetailsActions(
                    onBack = { navController.popBackStack() },
                    onContinue = { description, photoUris ->
                        sigDescription = description
                        sigPhotos = photoUris
                        navController.navigate(IrayRoute.SIGNALEMENT_CONFIRM)
                    },
                ),
            )
        }
        composable(IrayRoute.SIGNALEMENT_CONFIRM) {
            SignalementConfirmScreen(
                categoryId = sigCategoryId,
                subcategory = sigSubcategory,
                address = sigAddress,
                description = sigDescription,
                actions = SignalementConfirmActions(
                    onBack = { navController.popBackStack() },
                    onSend = {
                        requestController.submitSignalement(
                            category = sigCategoryId,
                            subcategory = sigSubcategory,
                            description = sigDescription,
                            zoneLabel = sigAddress,
                            latitude = sigLatitude,
                            longitude = sigLongitude,
                            photoUris = sigPhotos,
                        ) {
                            navController.navigate(IrayRoute.SIGNALEMENT_SUCCESS)
                        }
                    },
                ),
            )
        }
        composable(IrayRoute.SIGNALEMENT_SUCCESS) {
            SignalementSuccessScreen(
                categoryId = sigCategoryId,
                subcategory = sigSubcategory,
                referenceNumber = lastSignalementRef,
                actions = SignalementSuccessActions(
                    onBack = { navController.popBackStack() },
                    onViewReports = {
                        navController.navigate(IrayRoute.MES_SIGNALEMENTS) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onHome = { navController.navigate(IrayRoute.WELCOME) },
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
                        val documentNames = (0 until docCount).mapNotNull { index ->
                            attachedByKey["$prefix$index"]
                        }
                        requestController.submitRequest(
                            procedureId = demarcheId,
                            documentNames = documentNames,
                            territoryId = "$commune|$fokontany",
                        ) {
                            navController.navigate("confirmation/$demarcheId")
                        }
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
                dossierNumber = lastRequestRef,
                actions = ConfirmationActions(
                    onBack = { navController.popBackStack() },
                    onViewRequests = {
                        navController.navigate(IrayRoute.MES_DEMARCHES) {
                            popUpTo(IrayRoute.WELCOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
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
                initialLastName = profile?.lastName?.takeIf { it.isNotBlank() } ?: lastName,
                initialFirstName = profile?.firstName?.takeIf { it.isNotBlank() } ?: firstName,
                initialBirthdate = profile?.birthdate?.takeIf { it.isNotBlank() } ?: birthdate,
                initialAddress = profile?.fokontany?.takeIf { it.isNotBlank() } ?: fokontany,
                asksBirthPlace = DemarcheDetailsCatalog
                    .get(formDemarcheId)
                    .asksBirthPlace,
                actions = FormActions(
                    onBack = { navController.popBackStack() },
                    onSubmit = { formData ->
                        requestController.setDraftFormData(
                            mapOf(
                                "lastName" to formData.lastName,
                                "firstName" to formData.firstName,
                                "birthdate" to formData.birthdate,
                                "address" to formData.address,
                                "birthPlace" to formData.birthPlace,
                            ),
                        )
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
                    onFileConfirmed = { fileName, _ ->
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
