package mg.iray.app.ui.components.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.irayFastClick
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayTheme

/**
 * Cloche de notification + pastille rouge si non lu.
 */
@Composable
fun WelcomeNotificationBell(
    hasUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .irayFastClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BadgedBox(
            badge = {
                if (hasUnread) {
                    Badge(containerColor = FlagRed)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = stringResource(R.string.welcome_notifications_cd),
                tint = FlagGreen,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeNotificationBellPreview() {
    IrayTheme {
        WelcomeNotificationBell(hasUnread = true, onClick = {})
    }
}
