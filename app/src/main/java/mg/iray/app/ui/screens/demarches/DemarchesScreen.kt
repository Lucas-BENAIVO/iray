package mg.iray.app.ui.screens.demarches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.demarches.DemarcheFilterChip
import mg.iray.app.ui.components.demarches.DemarcheRow
import mg.iray.app.ui.components.zone.ZoneSearchField
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary

/**
 * Écran "Démarches administratives" — copie la capture du 11/09 15:24.
 *
 * Recherche + filtres + liste sélectionnable. Best practice : recherche,
 * filtre et sélection hoistés ici (rememberSaveable), composants de
 * [mg.iray.app.ui.components.demarches] stateless.
 */
data class DemarchesActions(
    val onBack: () -> Unit = {},
    val onDemarcheClick: (String) -> Unit = {},
)

enum class DemarcheCategory {
    DOCUMENTS,
    IDENTITE,
    AUTRES,
}

enum class DemarcheFilter {
    ALL,
    DOCUMENTS,
    IDENTITE,
    AUTRES,
}

data class DemarcheItem(
    val id: String,
    val label: String,
    val category: DemarcheCategory,
    val icon: ImageVector,
)

@Composable
fun DemarchesScreen(
    modifier: Modifier = Modifier,
    actions: DemarchesActions = DemarchesActions(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(DemarcheFilter.ALL) }
    // Choix par défaut = capture ("Certificat de résidence").
    var selectedId by rememberSaveable { mutableStateOf("certificat-residence") }

    val allItems = demoDemarches()
    val visibleItems = allItems.filter { item ->
        val matchesFilter = when (filter) {
            DemarcheFilter.ALL -> true
            DemarcheFilter.DOCUMENTS -> item.category == DemarcheCategory.DOCUMENTS
            DemarcheFilter.IDENTITE -> item.category == DemarcheCategory.IDENTITE
            DemarcheFilter.AUTRES -> item.category == DemarcheCategory.AUTRES
        }
        val matchesQuery = query.isBlank() ||
            item.label.contains(query.trim(), ignoreCase = true)
        matchesFilter && matchesQuery
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
                .padding(start = 8.dp, end = 24.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = actions.onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.demarches_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.demarches_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recherche réutilisée (même style que l’écran zone).
        ZoneSearchField(
            value = query,
            onValueChange = { query = it },
            hint = stringResource(R.string.demarches_search_hint),
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
                    label = stringResource(R.string.demarches_filter_all),
                    selected = filter == DemarcheFilter.ALL,
                    onClick = { filter = DemarcheFilter.ALL },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.demarches_filter_documents),
                    selected = filter == DemarcheFilter.DOCUMENTS,
                    onClick = { filter = DemarcheFilter.DOCUMENTS },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.demarches_filter_identite),
                    selected = filter == DemarcheFilter.IDENTITE,
                    onClick = { filter = DemarcheFilter.IDENTITE },
                )
            }
            item {
                DemarcheFilterChip(
                    label = stringResource(R.string.demarches_filter_autres),
                    selected = filter == DemarcheFilter.AUTRES,
                    onClick = { filter = DemarcheFilter.AUTRES },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp,
            ),
        ) {
            items(visibleItems, key = { it.id }) { item ->
                DemarcheRow(
                    label = item.label,
                    icon = item.icon,
                    selected = item.id == selectedId,
                    onClick = {
                        selectedId = item.id
                        actions.onDemarcheClick(item.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun demoDemarches(): List<DemarcheItem> {
    return listOf(
        DemarcheItem(
            id = "acte-naissance",
            label = stringResource(R.string.demarches_1_label),
            category = DemarcheCategory.DOCUMENTS,
            icon = Icons.Filled.Description,
        ),
        DemarcheItem(
            id = "certificat-residence",
            label = stringResource(R.string.demarches_2_label),
            category = DemarcheCategory.DOCUMENTS,
            icon = Icons.Filled.Home,
        ),
        DemarcheItem(
            id = "copie-acte-naissance",
            label = stringResource(R.string.demarches_3_label),
            category = DemarcheCategory.DOCUMENTS,
            icon = Icons.Filled.Description,
        ),
        DemarcheItem(
            id = "cin",
            label = stringResource(R.string.demarches_4_label),
            category = DemarcheCategory.IDENTITE,
            icon = Icons.Filled.CreditCard,
        ),
        DemarcheItem(
            id = "certificat-residence-2",
            label = stringResource(R.string.demarches_5_label),
            category = DemarcheCategory.DOCUMENTS,
            icon = Icons.Filled.Home,
        ),
        DemarcheItem(
            id = "autres",
            label = stringResource(R.string.demarches_6_label),
            category = DemarcheCategory.AUTRES,
            icon = Icons.Filled.MoreHoriz,
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun DemarchesScreenPreview() {
    IrayTheme {
        DemarchesScreen()
    }
}
