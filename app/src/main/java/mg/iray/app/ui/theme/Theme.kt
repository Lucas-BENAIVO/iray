package mg.iray.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandAccent,
    onPrimary = TextOnBrand,
    secondary = BrandDanger,
    onSecondary = TextOnBrand,
    tertiary = FlagGreen,
    onTertiary = TextOnBrand,
    background = SurfacePage,
    onBackground = TextPrimary,
    surface = SurfacePage,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceFeatured,
    onSurfaceVariant = TextSecondary,
    outline = DividerSubtle,
    error = BrandDanger,
    onError = TextOnBrand
)

@Composable
fun IrayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
