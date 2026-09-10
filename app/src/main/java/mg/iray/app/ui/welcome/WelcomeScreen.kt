package mg.iray.app.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import mg.iray.app.R
import mg.iray.app.ui.components.AppLogo
import mg.iray.app.ui.components.FeaturedBanner
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.components.WelcomeActionCard
import mg.iray.app.ui.components.WelcomeActionStyle
import mg.iray.app.ui.theme.BrandGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

data class WelcomeActions(
    val onAskQuestion: () -> Unit = {},
    val onFollowAnswers: () -> Unit = {},
    val onConsultations: () -> Unit = {},
    val onFeaturedCta: () -> Unit = {}
)

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    actions: WelcomeActions = WelcomeActions()
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

            Text(
                text = stringResource(R.string.welcome_greeting),
                style = MaterialTheme.typography.headlineLarge,
                color = BrandGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

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
            title = stringResource(R.string.welcome_action_ask_question),
            icon = Icons.AutoMirrored.Outlined.Chat,
            style = WelcomeActionStyle.FlagWhite,
            onClick = actions.onAskQuestion,
            modifier = Modifier.zIndex(1f)
        )
        WelcomeActionCard(
            title = stringResource(R.string.welcome_action_follow_answers),
            icon = Icons.Outlined.Forum,
            style = WelcomeActionStyle.FlagRed,
            onClick = actions.onFollowAnswers,
            modifier = Modifier.zIndex(3f)
        )
        WelcomeActionCard(
            title = stringResource(R.string.welcome_action_consultations),
            icon = Icons.Outlined.EditNote,
            style = WelcomeActionStyle.FlagGreen,
            onClick = actions.onConsultations,
            modifier = Modifier.zIndex(2f)
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
