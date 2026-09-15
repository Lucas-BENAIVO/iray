package mg.iray.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextSecondary

enum class IrayBottomTab {
    Home,
    Demarches,
    Signalements,
    Profile,
}

data class IrayBottomBarActions(
    val onHome: () -> Unit = {},
    val onDemarches: () -> Unit = {},
    val onSignalements: () -> Unit = {},
    val onProfile: () -> Unit = {},
)

@Composable
fun IrayBottomBar(
    selected: IrayBottomTab,
    actions: IrayBottomBarActions,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        Triple(IrayBottomTab.Home, Icons.Outlined.Home, R.string.nav_home),
        Triple(IrayBottomTab.Demarches, Icons.Outlined.Description, R.string.nav_demarches),
        Triple(IrayBottomTab.Signalements, Icons.Outlined.Report, R.string.nav_signalements),
        Triple(IrayBottomTab.Profile, Icons.Outlined.Person, R.string.nav_profile),
    )

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = BrandWhite,
        tonalElevation = 0.dp,
    ) {
        items.forEach { (tab, icon, labelRes) ->
            val isSelected = tab == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (tab) {
                        IrayBottomTab.Home -> actions.onHome()
                        IrayBottomTab.Demarches -> actions.onDemarches()
                        IrayBottomTab.Signalements -> actions.onSignalements()
                        IrayBottomTab.Profile -> actions.onProfile()
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(labelRes),
                        modifier = Modifier.size(20.dp),
                    )
                },
                label = {
                    Text(
                        text = stringResource(labelRes),
                        fontFamily = IrayFontFamily,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FlagGreen,
                    selectedTextColor = FlagGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = FlagGreen.copy(alpha = 0.12f),
                ),
            )
        }
    }
}

@Preview
@Composable
private fun IrayBottomBarPreview() {
    IrayTheme {
        IrayBottomBar(
            selected = IrayBottomTab.Home,
            actions = IrayBottomBarActions(),
        )
    }
}
