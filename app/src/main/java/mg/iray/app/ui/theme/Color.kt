package mg.iray.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette État malgache — drapeau national :
 * Blanc → Rouge → Vert
 */
val BrandWhite = Color(0xFFFFFFFF)
val BrandRed = Color(0xFFFC3D32)
val BrandGreen = Color(0xFF007E3A)

/** @deprecated Prefer [BrandGreen] — kept for gradual migration. */
@Deprecated("Use BrandGreen", ReplaceWith("BrandGreen"))
val BrandBlue = BrandGreen

val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF4A4A4A)
val TextOnBrand = Color(0xFFFFFFFF)

val SurfacePage = Color(0xFFFFFFFF)
/** Soft green wash — echoes the national green without competing with CTAs. */
val SurfaceFeatured = Color(0xFFE8F5EE)
val SurfaceCard = Color(0xFFFFFFFF)
/** Very light red tint for secondary surfaces if needed. */
val SurfaceAccentSoft = Color(0xFFFFF1F0)

val DividerSubtle = Color(0xFFE5E5E5)
val OutlineOnWhite = Color(0xFFD0D0D0)
