package mg.iray.app.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@Composable
fun OnboardingBottomContent(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = 1.5.dp,
                    color = FlagGreen.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(14.dp),
                )
                .background(
                    color = Color.Black.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(14.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Hub,
                contentDescription = null,
                tint = FlagGreen,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_headline),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 42.sp,
                letterSpacing = (-0.6).sp,
            ),
            color = BrandWhite,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.welcome_onboarding_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                lineHeight = 24.sp,
            ),
            color = BrandWhite.copy(alpha = 0.86f),
        )

        Spacer(modifier = Modifier.height(30.dp))

        IrayPrimaryButton(
            label = stringResource(R.string.welcome_hero_cta),
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(999.dp),
                    ambientColor = FlagGreen.copy(alpha = 0.35f),
                    spotColor = FlagGreen.copy(alpha = 0.45f),
                ),
        )

        Spacer(modifier = Modifier.height(20.dp))

        FlagAccentBar(
            modifier = Modifier.fillMaxWidth(),
            height = 4.dp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A1510)
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
