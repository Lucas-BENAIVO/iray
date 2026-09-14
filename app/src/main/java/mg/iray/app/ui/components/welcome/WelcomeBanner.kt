package mg.iray.app.ui.components.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextOnBrand

/**
 * Bannière Welcome premium — photo pleine largeur, coins très arrondis, typo Clash.
 */
@Composable
fun WelcomeBanner(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = OnboardingInk.copy(alpha = 0.22f),
                spotColor = OnboardingInk.copy(alpha = 0.18f),
            )
            .clip(shape),
    ) {
        Image(
            painter = painterResource(R.drawable.welcome_banner),
            contentDescription = stringResource(R.string.welcome_content_desc_banner),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.15f),
                            0.45f to Color.Black.copy(alpha = 0.35f),
                            1.0f to Color.Black.copy(alpha = 0.72f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.welcome_banner_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.3).sp,
                ),
                color = TextOnBrand,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_banner_body),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = IrayFontFamily,
                    lineHeight = 20.sp,
                ),
                color = TextOnBrand.copy(alpha = 0.9f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeBannerPreview() {
    IrayTheme {
        WelcomeBanner(modifier = Modifier.padding(16.dp))
    }
}
