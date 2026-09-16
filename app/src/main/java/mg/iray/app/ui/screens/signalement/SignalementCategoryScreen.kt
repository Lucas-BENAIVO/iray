package mg.iray.app.ui.screens.signalement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.signalement.SignalementCategoryRow
import mg.iray.app.ui.components.zone.ZoneSearchField
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage

/**
 * Écran "Signaler un problème" - choix de la catégorie, recherche live.
 *
 * Best practice : recherche et sélection hoistées ici (rememberSaveable),
 * lignes de [mg.iray.app.ui.components.signalement] stateless.
 */
data class SignalementCategoryActions(
    val onBack: () -> Unit = {},
    val onCategoryClick: (String) -> Unit = {},
)

@Composable
fun SignalementCategoryScreen(
    modifier: Modifier = Modifier,
    actions: SignalementCategoryActions = SignalementCategoryActions(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    // Choix par défaut = capture ("Routes et voirie").
    var selectedId by rememberSaveable { mutableStateOf("routes") }

    val allCategories = SignalementCatalog.categories
    val visibleCategories = allCategories.filter { category ->
        query.isBlank() ||
            stringResource(category.labelRes).contains(query.trim(), ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.signalement_title),
            subtitle = stringResource(R.string.signalement_subtitle),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(4.dp))

        ZoneSearchField(
            value = query,
            onValueChange = { query = it },
            hint = stringResource(R.string.signalement_search_hint),
            accentColor = FlagRed,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp,
            ),
        ) {
            items(visibleCategories, key = { it.id }) { category ->
                SignalementCategoryRow(
                    label = stringResource(category.labelRes),
                    icon = category.icon,
                    selected = category.id == selectedId,
                    onClick = {
                        selectedId = category.id
                        actions.onCategoryClick(category.id)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementCategoryScreenPreview() {
    IrayTheme {
        SignalementCategoryScreen()
    }
}
