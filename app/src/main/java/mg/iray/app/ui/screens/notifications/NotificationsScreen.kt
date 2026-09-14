package mg.iray.app.ui.screens.notifications

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.notifications.NotificationCard
import mg.iray.app.ui.components.notifications.NotificationFilterChip
import mg.iray.app.ui.theme.BrandDanger
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SuccessAvatar
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary

/**
 * Écran Notifications — copie la capture du 11/09 15:01.
 *
 * Filtres + liste. Best practice : filtre hoisté ici (rememberSaveable),
 * données de démo fournies par défaut (à remplacer par le repository).
 */
data class NotificationsActions(
    val onBack: () -> Unit = {},
    val onFilterClick: () -> Unit = {},
    val onNotificationClick: (String) -> Unit = {},
)

enum class NotificationCategory {
    DEMARCHE,
    SIGNALEMENT,
    OFFICIELLE,
    PROFIL,
}

enum class NotificationFilter {
    ALL,
    UNREAD,
    DEMARCHES,
    SIGNALEMENTS,
}

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val isRead: Boolean,
    val title: String,
    val body: String,
    val time: String,
    val icon: ImageVector,
    val iconTint: Color,
)

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    actions: NotificationsActions = NotificationsActions(),
    notifications: List<AppNotification> = emptyList(),
) {
    var filter by rememberSaveable { mutableStateOf(NotificationFilter.ALL) }

    val demoNotifications = demoNotifications()
    val items = (if (notifications.isEmpty()) demoNotifications else notifications)
        .filter { notification ->
            when (filter) {
                NotificationFilter.ALL -> true
                NotificationFilter.UNREAD -> !notification.isRead
                NotificationFilter.DEMARCHES -> notification.category == NotificationCategory.DEMARCHE
                NotificationFilter.SIGNALEMENTS -> notification.category == NotificationCategory.SIGNALEMENT
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = actions.onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.notifications_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.notifications_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = actions.onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = stringResource(R.string.notifications_filter_cd),
                    tint = TextPrimary,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            item {
                NotificationFilterChip(
                    label = stringResource(R.string.notifications_filter_all),
                    selected = filter == NotificationFilter.ALL,
                    onClick = { filter = NotificationFilter.ALL },
                )
            }
            item {
                NotificationFilterChip(
                    label = stringResource(R.string.notifications_filter_unread),
                    selected = filter == NotificationFilter.UNREAD,
                    onClick = { filter = NotificationFilter.UNREAD },
                )
            }
            item {
                NotificationFilterChip(
                    label = stringResource(R.string.notifications_filter_demarches),
                    selected = filter == NotificationFilter.DEMARCHES,
                    onClick = { filter = NotificationFilter.DEMARCHES },
                )
            }
            item {
                NotificationFilterChip(
                    label = stringResource(R.string.notifications_filter_signalements),
                    selected = filter == NotificationFilter.SIGNALEMENTS,
                    onClick = { filter = NotificationFilter.SIGNALEMENTS },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp,
            ),
        ) {
            items(items, key = { it.id }) { notification ->
                NotificationCard(
                    title = notification.title,
                    body = notification.body,
                    time = notification.time,
                    icon = notification.icon,
                    iconTint = notification.iconTint,
                )
            }
        }
    }
}

@Composable
private fun demoNotifications(): List<AppNotification> {
    return listOf(
        AppNotification(
            id = "1",
            category = NotificationCategory.DEMARCHE,
            isRead = false,
            title = stringResource(R.string.notifications_1_title),
            body = stringResource(R.string.notifications_1_body),
            time = stringResource(R.string.notifications_1_time),
            icon = Icons.Filled.Description,
            iconTint = FlagGreen,
        ),
        AppNotification(
            id = "2",
            category = NotificationCategory.SIGNALEMENT,
            isRead = false,
            title = stringResource(R.string.notifications_2_title),
            body = stringResource(R.string.notifications_2_body),
            time = stringResource(R.string.notifications_2_time),
            icon = Icons.Filled.Whatshot,
            iconTint = SuccessAvatar,
        ),
        AppNotification(
            id = "3",
            category = NotificationCategory.OFFICIELLE,
            isRead = true,
            title = stringResource(R.string.notifications_3_title),
            body = stringResource(R.string.notifications_3_body),
            time = stringResource(R.string.notifications_3_time),
            icon = Icons.Filled.Warning,
            iconTint = BrandDanger,
        ),
        AppNotification(
            id = "4",
            category = NotificationCategory.DEMARCHE,
            isRead = true,
            title = stringResource(R.string.notifications_4_title),
            body = stringResource(R.string.notifications_4_body),
            time = stringResource(R.string.notifications_4_time),
            icon = Icons.Filled.AssignmentTurnedIn,
            iconTint = FlagGreen,
        ),
        AppNotification(
            id = "5",
            category = NotificationCategory.PROFIL,
            isRead = true,
            title = stringResource(R.string.notifications_5_title),
            body = stringResource(R.string.notifications_5_body),
            time = stringResource(R.string.notifications_5_time),
            icon = Icons.Filled.Person,
            iconTint = SuccessAvatar,
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun NotificationsScreenPreview() {
    IrayTheme {
        NotificationsScreen()
    }
}
