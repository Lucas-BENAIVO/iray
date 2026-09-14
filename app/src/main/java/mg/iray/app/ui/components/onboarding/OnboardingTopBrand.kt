package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayTheme

/**
 * Marque en haut à gauche : petit logo + Iray + barre drapeau.
 */
@Composable
fun OnboardingTopBrand(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_republique),
                contentDescription = stringResource(R.string.welcome_content_desc_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.welcome_hero_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    fontSize = 18.sp,
                ),
                color = BrandWhite.copy(alpha = 0.98f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlagAccentBar(
            modifier = Modifier.fillMaxWidth(0.22f),
            height = 3.dp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
private fun OnboardingTopBrandPreview() {
    IrayTheme {
        OnboardingTopBrand(modifier = Modifier.fillMaxWidth())
    }
}
