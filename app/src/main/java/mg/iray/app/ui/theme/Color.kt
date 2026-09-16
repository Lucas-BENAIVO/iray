package mg.iray.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design system État malgache - palette institutionnelle maximale.
 *
 * Drapeau = identité (logo, barre). UI = neutres + un vert très retenu.
 */

// -- Drapeau (marque d'État uniquement)
val FlagWhite = Color(0xFFFFFFFF)
val FlagRed = Color(0xFFFC3D32)
val FlagGreen = Color(0xFF007E3A)

// -- UI
/** Vert d'État très désaturé - icônes, liens, liserés. */
val BrandAccent = Color(0xFF184A38)
/** Fond primaire : à peine teinté, presque blanc. */
val BrandPrimarySoft = Color(0xFFF4F7F5)
/** Alertes. */
val BrandDanger = Color(0xFF9B1C1C)

val BrandWhite = FlagWhite

@Deprecated("Use BrandAccent", ReplaceWith("BrandAccent"))
val BrandGreen = BrandAccent
@Deprecated("Use BrandDanger or FlagRed", ReplaceWith("BrandDanger"))
val BrandRed = BrandDanger
@Deprecated("Use BrandAccent", ReplaceWith("BrandAccent"))
val BrandInk = BrandAccent
@Deprecated("Use BrandAccent", ReplaceWith("BrandAccent"))
val BrandBlue = BrandAccent

val TextPrimary = Color(0xFF161616)
val TextSecondary = Color(0xFF666666)
val TextOnBrand = Color(0xFFFFFFFF)

val SurfacePage = Color(0xFFFFFFFF)
val SurfaceFeatured = Color(0xFFF6F6F6)
val SurfaceCard = Color(0xFFFFFFFF)

val DividerSubtle = Color(0xFFEEEEEE)
val OutlineOnWhite = Color(0xFFE0E0E0)

// -- Carte zone (placeholder en attendant le SDK carto)
/** Fond beige clair façon carte. */
val ZoneMapBackground = Color(0xFFEDE9DC)
/** Punaise de localisation bleue. */
val ZoneMapPin = Color(0xFF1A73E8)

// -- Écran succès
/** Halo vert clair autour du check. */
val SuccessHalo = Color(0xFFE3F3E8)
/** Pastille avatar bleu nuit. */
val SuccessAvatar = Color(0xFF1B3A5C)

// -- Priorités signalement
/** Orange priorité moyenne. */
val PriorityOrange = Color(0xFFE68900)

// -- Onboarding premium
/** Vert profond atmosphérique. */
val OnboardingGreenDeep = Color(0xFF123528)
/** Vert doux bas d'écran. */
val OnboardingGreenSoft = Color(0xFF3A634C)
/** Texte secondaire sur fond vert. */
val OnboardingMutedOnGreen = Color(0xFFC5D6CB)
/** Chrome premium (CTA / barre). */
val OnboardingInk = Color(0xFF111111)
