package mg.iray.app.ui.screens.mesSignalements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayBottomBar
import mg.iray.app.ui.components.IrayBottomBarActions
import mg.iray.app.ui.components.IrayBottomTab
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.demarches.DemarcheFilterChip
import mg.iray.app.ui.components.mes.MesRequestCard
import mg.iray.app.ui.components.mes.MesStatusColors
import mg.iray.app.ui.components.zone.ZoneSearchField
import mg.iray.app.ui.theme.BrandDanger
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Mes signalements" — historique des signalements envoyés.
 *
 * Même logique que Mes démarches : recherche, filtres, liste, CTA, bottom bar.
 */
data class MesSignalementsActions(
    val onBack: () -> Unit = {},
    val onHome: () -> Unit = {},
    val onDemarches: () -> Unit = {},
    val onProfile: () -> Unit = {},
    val onNewReport: () -> Unit = {},
    val onReportClick: (String) -> Unit = {},
)

enum class MesSignalementStatus {
    RECEIVED,
    ONGOING,
    RESOLVED,
}

enum class MesSignalementFilter {
    ALL,
    ONGOING,
    RESOLVED,
}

data class MesSignalementItem(
    val id: String,
    val dossierNumber: String,
    val title: String,
    val body: String,
    val date: String,
    val status: MesSignalementStatus,
    val icon: ImageVector,
)

@Composable
fun MesSignalementsScreen(
    modifier: Modifier = Modifier,
    actions: MesSignalementsActions = MesSignalementsActions(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(MesSignalementFilter.ALL) }

    val items = demoMesSignalements().filter { item ->
        val matchesFilter = when (filter) {
            MesSignalementFilter.ALL -> true
            MesSignalementFilter.ONGOING -> item.status != MesSignalementStatus.RESOLVED
            MesSignalementFilter.RESOLVED -> item.status == MesSignalementStatus.RESOLVED
        }
        val matchesQuery = query.isBlank() ||
            item.title.contains(query.trim(), ignoreCase = true) ||
            item.body.contains(query.trim(), ignoreCase = true) ||
            item.dossierNumber.contains(query.trim(), ignoreCase = true)
        matchesFilter && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.mes_signalements_title),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(4.dp))

        ZoneSearchField(
            value = query,
            onValueChange = { query = it },
            hint = stringResource(R.string.mes_signalements_search_hint),
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.mes_signalements_filter_all),
                    selected = filter == MesSignalementFilter.ALL,
                    onClick = { filter = MesSignalementFilter.ALL },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.mes_signalements_filter_ongoing),
                    selected = filter == MesSignalementFilter.ONGOING,
                    onClick = { filter = MesSignalementFilter.ONGOING },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.mes_signalements_filter_resolved),
                    selected = filter == MesSignalementFilter.RESOLVED,
                    onClick = { filter = MesSignalementFilter.RESOLVED },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.mes_signalements_empty),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = TextSecondary,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp,
                ),
            ) {
                items(items, key = { it.id }) { item ->
                    val (statusLabel, statusTint) = when (item.status) {
                        MesSignalementStatus.RECEIVED ->
                            stringResource(R.string.signalement_success_received_label) to
                                MesStatusColors.Received
                        MesSignalementStatus.ONGOING ->
                            stringResource(R.string.signalement_step_ongoing) to
                                MesStatusColors.Ongoing
                        MesSignalementStatus.RESOLVED ->
                            stringResource(R.string.signalement_step_resolved) to
                                MesStatusColors.Done
                    }
                    MesRequestCard(
                        title = item.title,
                        subtitle = item.body,
                        dossierLabel = stringResource(
                            R.string.mes_signalements_dossier_format,
                            item.dossierNumber,
                        ),
                        date = item.date,
                        statusLabel = statusLabel,
                        statusTint = statusTint,
                        icon = item.icon,
                        iconTint = BrandDanger,
                        onClick = { actions.onReportClick(item.id) },
                    )
                }
            }
        }

        IrayPrimaryButton(
            label = stringResource(R.string.mes_signalements_cta),
            onClick = actions.onNewReport,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp),
        )

        IrayBottomBar(
            selected = IrayBottomTab.Signalements,
            actions = IrayBottomBarActions(
                onHome = actions.onHome,
                onDemarches = actions.onDemarches,
                onSignalements = {},
                onProfile = actions.onProfile,
            ),
        )
    }
}

@Composable
private fun demoMesSignalements(): List<MesSignalementItem> {
    return listOf(
        MesSignalementItem(
            id = "sig-1",
            dossierNumber = "SIG-2026-291104",
            title = stringResource(R.string.mes_signalements_1_title),
            body = stringResource(R.string.mes_signalements_1_body),
            date = stringResource(R.string.mes_signalements_1_date),
            status = MesSignalementStatus.ONGOING,
            icon = Icons.Filled.Lightbulb,
        ),
        MesSignalementItem(
            id = "sig-2",
            dossierNumber = "SIG-2026-278551",
            title = stringResource(R.string.mes_signalements_2_title),
            body = stringResource(R.string.mes_signalements_2_body),
            date = stringResource(R.string.mes_signalements_2_date),
            status = MesSignalementStatus.RECEIVED,
            icon = Icons.Filled.Delete,
        ),
        MesSignalementItem(
            id = "sig-3",
            dossierNumber = "SIG-2026-265033",
            title = stringResource(R.string.mes_signalements_3_title),
            body = stringResource(R.string.mes_signalements_3_body),
            date = stringResource(R.string.mes_signalements_3_date),
            status = MesSignalementStatus.RESOLVED,
            icon = Icons.Filled.Warning,
        ),
        MesSignalementItem(
            id = "sig-4",
            dossierNumber = "SIG-2026-251880",
            title = stringResource(R.string.mes_signalements_1_title),
            body = stringResource(R.string.mes_signalements_1_body),
            date = stringResource(R.string.mes_signalements_3_date),
            status = MesSignalementStatus.RESOLVED,
            icon = Icons.Filled.Report,
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun MesSignalementsScreenPreview() {
    IrayTheme {
        MesSignalementsScreen()
    }
}
