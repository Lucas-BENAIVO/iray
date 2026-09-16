package mg.iray.app.ui.screens.mesDemarches

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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
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
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Mes démarches" - historique des demandes soumises.
 *
 * Recherche + filtres de statut + liste + CTA nouvelle demande + bottom bar.
 * Best practice : état UI hoisté ici, données démo (à remplacer par repo).
 */
data class MesDemarchesActions(
    val onBack: () -> Unit = {},
    val onHome: () -> Unit = {},
    val onSignalements: () -> Unit = {},
    val onProfile: () -> Unit = {},
    val onNewRequest: () -> Unit = {},
    val onRequestClick: (String) -> Unit = {},
)

enum class MesDemarcheStatus {
    RECEIVED,
    ONGOING,
    DONE,
}

enum class MesDemarcheFilter {
    ALL,
    ONGOING,
    DONE,
}

data class MesDemarcheItem(
    val id: String,
    val dossierNumber: String,
    val title: String,
    val service: String,
    val date: String,
    val status: MesDemarcheStatus,
    val icon: ImageVector,
)

@Composable
fun MesDemarchesScreen(
    items: List<MesDemarcheItem> = emptyList(),
    modifier: Modifier = Modifier,
    actions: MesDemarchesActions = MesDemarchesActions(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(MesDemarcheFilter.ALL) }

    val source = items
    val filtered = source.filter { item ->
        val matchesFilter = when (filter) {
            MesDemarcheFilter.ALL -> true
            MesDemarcheFilter.ONGOING -> item.status != MesDemarcheStatus.DONE
            MesDemarcheFilter.DONE -> item.status == MesDemarcheStatus.DONE
        }
        val matchesQuery = query.isBlank() ||
            item.title.contains(query.trim(), ignoreCase = true) ||
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
            title = stringResource(R.string.mes_demarches_title),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(4.dp))

        ZoneSearchField(
            value = query,
            onValueChange = { query = it },
            hint = stringResource(R.string.mes_demarches_search_hint),
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
                    label = stringResource(R.string.mes_demarches_filter_all),
                    selected = filter == MesDemarcheFilter.ALL,
                    onClick = { filter = MesDemarcheFilter.ALL },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.mes_demarches_filter_ongoing),
                    selected = filter == MesDemarcheFilter.ONGOING,
                    onClick = { filter = MesDemarcheFilter.ONGOING },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.mes_demarches_filter_done),
                    selected = filter == MesDemarcheFilter.DONE,
                    onClick = { filter = MesDemarcheFilter.DONE },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.mes_demarches_empty),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = TextSecondary,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp,
                ),
            ) {
                items(filtered, key = { it.id }) { item ->
                    val (statusLabel, statusTint) = when (item.status) {
                        MesDemarcheStatus.RECEIVED ->
                            stringResource(R.string.confirmation_received_label) to
                                MesStatusColors.Received
                        MesDemarcheStatus.ONGOING ->
                            stringResource(R.string.confirmation_step_processing) to
                                MesStatusColors.Ongoing
                        MesDemarcheStatus.DONE ->
                            stringResource(R.string.confirmation_step_done) to
                                MesStatusColors.Done
                    }
                    MesRequestCard(
                        title = item.title,
                        subtitle = item.service,
                        dossierLabel = stringResource(
                            R.string.mes_demarches_dossier_format,
                            item.dossierNumber,
                        ),
                        date = item.date,
                        statusLabel = statusLabel,
                        statusTint = statusTint,
                        icon = item.icon,
                        iconTint = FlagGreen,
                        onClick = { actions.onRequestClick(item.id) },
                    )
                }
            }
        }

        IrayPrimaryButton(
            label = stringResource(R.string.mes_demarches_cta),
            onClick = actions.onNewRequest,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp),
        )

        IrayBottomBar(
            selected = IrayBottomTab.Demarches,
            actions = IrayBottomBarActions(
                onHome = actions.onHome,
                onDemarches = {},
                onSignalements = actions.onSignalements,
                onProfile = actions.onProfile,
            ),
        )
    }
}

@Composable
private fun demoMesDemarches(): List<MesDemarcheItem> {
    return listOf(
        MesDemarcheItem(
            id = "mad-1",
            dossierNumber = "MAD-2026-184221",
            title = stringResource(R.string.mes_demarches_1_title),
            service = stringResource(R.string.mes_demarches_1_service),
            date = stringResource(R.string.mes_demarches_1_date),
            status = MesDemarcheStatus.ONGOING,
            icon = Icons.Filled.Home,
        ),
        MesDemarcheItem(
            id = "mad-2",
            dossierNumber = "MAD-2026-172048",
            title = stringResource(R.string.mes_demarches_2_title),
            service = stringResource(R.string.mes_demarches_2_service),
            date = stringResource(R.string.mes_demarches_2_date),
            status = MesDemarcheStatus.RECEIVED,
            icon = Icons.Filled.Description,
        ),
        MesDemarcheItem(
            id = "mad-3",
            dossierNumber = "MAD-2026-159331",
            title = stringResource(R.string.mes_demarches_3_title),
            service = stringResource(R.string.mes_demarches_3_service),
            date = stringResource(R.string.mes_demarches_3_date),
            status = MesDemarcheStatus.DONE,
            icon = Icons.Filled.CreditCard,
        ),
        MesDemarcheItem(
            id = "mad-4",
            dossierNumber = "MAD-2026-148902",
            title = stringResource(R.string.mes_demarches_1_title),
            service = stringResource(R.string.mes_demarches_1_service),
            date = stringResource(R.string.mes_demarches_3_date),
            status = MesDemarcheStatus.DONE,
            icon = Icons.Filled.AssignmentTurnedIn,
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun MesDemarchesScreenPreview() {
    IrayTheme {
        MesDemarchesScreen(items = demoMesDemarches())
    }
}
