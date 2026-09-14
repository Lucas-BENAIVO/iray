package mg.iray.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * En-tête d’écran — design system onboarding (Clash titre, Satoshi sous-titre, barre drapeau).
 */
@Composable
fun IrayScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    showFlagBar: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 4.dp, bottom = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TextPrimary,
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(start = 16.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = (-0.2).sp,
                ),
                color = TextPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            )
        }

        if (showFlagBar) {
            FlagAccentBar(
                modifier = Modifier
                    .padding(start = if (onBack != null) 56.dp else 16.dp)
                    .fillMaxWidth(0.22f),
                height = 3.dp,
            )
        }

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = IrayFontFamily,
                    fontWeight = FontWeight.Normal,
                ),
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IrayScreenHeaderPreview() {
    IrayTheme {
        IrayScreenHeader(
            title = "Créons votre profil",
            subtitle = "Quelques informations pour personnaliser votre espace.",
            onBack = {},
        )
    }
}
