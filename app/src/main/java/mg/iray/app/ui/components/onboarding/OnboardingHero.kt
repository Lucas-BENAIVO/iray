package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Haut de page d’accueil — copie la capture e-Mada avec la marque Iray.
 *
 * - Fond photo [R.drawable.welcome_hero] (marché de Tana).
 * - Voile blanc haut/bas pour garder le texte lisible (best practice contraste).
 * - Logo République petit centré + titre "Iray" (et non e-Mada) + sous-titre.
 */
@Composable
fun OnboardingHero(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.welcome_hero),
            contentDescription = stringResource(R.string.welcome_content_desc_hero),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Voile lisibilité : blanc doux en haut (logo/titre), fusion blanc en bas (carte).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.94f),
                            Color.White.copy(alpha = 0.72f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.96f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_republique),
                contentDescription = stringResource(R.string.welcome_content_desc_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlagAccentBar(
                modifier = Modifier.fillMaxWidth(0.28f),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.welcome_hero_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.welcome_hero_subtitle),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun OnboardingHeroPreview() {
    IrayTheme {
        OnboardingHero()
    }
}
