package mg.iray.app.ui.components.welcome

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Cloche de notification bleue + pastille rouge si non lu.
 *
 * Copie la capture du 11/09 14:43. Best practice : stateless,
 * aucun texte en dur.
 */
@Composable
fun WelcomeNotificationBell(
    hasUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
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
                tint = ZoneMapPin,
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
