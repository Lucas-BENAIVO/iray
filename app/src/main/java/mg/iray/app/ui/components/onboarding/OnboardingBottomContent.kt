package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme

/**
 * Bloc bas immersif — icône, titre, sous-titre, CTA (style référence carpool).
 */
@Composable
fun OnboardingBottomContent(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountBalance,
            contentDescription = null,
            tint = FlagGreen,
            modifier = Modifier
                .size(44.dp)
                .border(
                    width = 1.5.dp,
                    color = FlagGreen.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(10.dp),
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_headline),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.4).sp,
            ),
            color = BrandWhite,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
            color = BrandWhite.copy(alpha = 0.82f),
        )

        Spacer(modifier = Modifier.height(28.dp))

        IrayPrimaryButton(
            label = stringResource(R.string.welcome_hero_cta),
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        FlagAccentBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            height = 4.dp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
private fun OnboardingBottomContentPreview() {
    IrayTheme {
        OnboardingBottomContent(
            onStartClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )
    }
}
