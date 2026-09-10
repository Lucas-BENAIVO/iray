package mg.iray.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = BrandWhite,
    secondary = BrandRed,
    onSecondary = BrandWhite,
    tertiary = BrandGreen,
    onTertiary = BrandWhite,
    background = SurfacePage,
    onBackground = TextPrimary,
    surface = SurfacePage,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceFeatured,
    onSurfaceVariant = TextSecondary,
    outline = DividerSubtle,
    error = BrandRed,
    onError = BrandWhite
)

/**
 * Thème Iray — identité visuelle de l’État malgache (clair uniquement).
 */
@Composable
fun IrayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
