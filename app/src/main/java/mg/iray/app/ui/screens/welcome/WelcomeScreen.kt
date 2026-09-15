package mg.iray.app.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.components.IrayBottomBar
import mg.iray.app.ui.components.IrayBottomBarActions
import mg.iray.app.ui.components.IrayBottomTab
import mg.iray.app.ui.components.welcome.FeaturedBanner
import mg.iray.app.ui.components.welcome.WelcomeActionCard
import mg.iray.app.ui.components.welcome.WelcomeActionStyle
import mg.iray.app.ui.components.welcome.WelcomeBanner
import mg.iray.app.ui.components.welcome.WelcomeNotificationBell
import mg.iray.app.ui.theme.BrandPrimarySoft
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextPrimary

data class WelcomeActions(
    val onDemarches: () -> Unit = {},
    val onSignalements: () -> Unit = {},
    val onMesDemarches: () -> Unit = {},
    val onMesSignalements: () -> Unit = {},
    val onNotifications: () -> Unit = {},
    val onProfile: () -> Unit = {},
)

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    actions: WelcomeActions = WelcomeActions(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrandWhite,
                        BrandPrimarySoft,
                        BrandPrimarySoft.copy(alpha = 0.65f),
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 14.dp, bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
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
                            letterSpacing = 2.6.sp,
                            fontSize = 18.sp,
                        ),
                        color = TextPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                FlagAccentBar(
                    modifier = Modifier
                        .fillMaxWidth(0.2f)
                        .align(Alignment.CenterHorizontally),
                    height = 3.dp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.welcome_greeting),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = IrayDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 23.sp,
                            letterSpacing = (-0.3).sp,
                        ),
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    WelcomeNotificationBell(
                        hasUnread = true,
                        onClick = actions.onNotifications,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                WelcomeBanner()

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = stringResource(R.string.welcome_actions_label),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = IrayDisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp,
                    ),
                    color = TextPrimary,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    WelcomeActionCard(
                        title = stringResource(R.string.welcome_action_demarches),
                        subtitle = stringResource(R.string.welcome_action_demarches_hint),
                        icon = Icons.Outlined.Description,
                        style = WelcomeActionStyle.Demarches,
                        onClick = actions.onDemarches,
                    )
                    WelcomeActionCard(
                        title = stringResource(R.string.welcome_action_signalements),
                        subtitle = stringResource(R.string.welcome_action_signalements_hint),
                        icon = Icons.Outlined.Report,
                        style = WelcomeActionStyle.Signalements,
                        onClick = actions.onSignalements,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            FeaturedBanner(
                title = stringResource(R.string.welcome_featured_title),
                body = stringResource(R.string.welcome_featured_body),
                modifier = Modifier.fillMaxWidth(),
            )

            IrayBottomBar(
                selected = IrayBottomTab.Home,
                actions = IrayBottomBarActions(
                    onHome = {},
                    onDemarches = actions.onMesDemarches,
                    onSignalements = actions.onMesSignalements,
                    onProfile = actions.onProfile,
                ),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun WelcomeScreenPreview() {
    IrayTheme {
        WelcomeScreen()
    }
}
