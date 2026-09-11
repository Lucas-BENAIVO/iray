package mg.iray.app.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Report
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
import androidx.compose.ui.zIndex
import mg.iray.app.R
import mg.iray.app.ui.components.AppLogo
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.components.welcome.FeaturedBanner
import mg.iray.app.ui.components.welcome.WelcomeActionCard
import mg.iray.app.ui.components.welcome.WelcomeActionStyle
import mg.iray.app.ui.components.welcome.WelcomeBanner
import mg.iray.app.ui.components.welcome.WelcomeNotificationBell
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

data class WelcomeActions(
    val onDemarches: () -> Unit = {},
    val onSignalements: () -> Unit = {},
    val onNotifications: () -> Unit = {},
    val onFeaturedCta: () -> Unit = {}
)

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    actions: WelcomeActions = WelcomeActions(),
    userFullName: String = "",
    userZoneLabel: String = "",
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AppLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlagAccentBar(
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (userFullName.isNotBlank()) {
                        "${stringResource(R.string.welcome_greeting)} $userFullName"
                    } else {
                        stringResource(R.string.welcome_greeting)
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                WelcomeNotificationBell(
                    hasUnread = true,
                    onClick = actions.onNotifications,
                )
            }

            if (userZoneLabel.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = stringResource(R.string.welcome_zone_cd),
                        tint = ZoneMapPin,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userZoneLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            WelcomeBanner()

            Spacer(modifier = Modifier.height(20.dp))

            WelcomeActionsStack(actions = actions)
        }

        FeaturedBanner(
            title = stringResource(R.string.welcome_featured_title),
            body = stringResource(R.string.welcome_featured_body),
            ctaLabel = stringResource(R.string.welcome_featured_cta),
            onCtaClick = actions.onFeaturedCta,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun WelcomeActionsStack(
    actions: WelcomeActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy((-6).dp)
    ) {
        WelcomeActionCard(
            title = stringResource(R.string.welcome_action_demarches),
            icon = Icons.Outlined.Description,
            style = WelcomeActionStyle.Primary,
            onClick = actions.onDemarches,
            modifier = Modifier.zIndex(1f)
        )
        WelcomeActionCard(
            title = stringResource(R.string.welcome_action_signalements),
            icon = Icons.Outlined.Report,
            style = WelcomeActionStyle.Quiet,
            onClick = actions.onSignalements,
            modifier = Modifier.zIndex(3f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun WelcomeScreenPreview() {
    IrayTheme {
        WelcomeScreen()
    }
}
