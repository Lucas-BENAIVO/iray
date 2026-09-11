package mg.iray.app.ui.screens.zone

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.zone.ZoneDropdownField
import mg.iray.app.ui.components.zone.ZoneMapPreview
import mg.iray.app.ui.components.zone.ZoneSearchField
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Votre zone" — entre création profil et welcome.
 *
 * Copie la capture du 11/09 14:06 : recherche + aperçu carte +
 * Région / District / Commune / Fokontany + CTA "Continuer".
 *
 * Best practice : état hoisté ici (rememberSaveable), composants de
 * [mg.iray.app.ui.components.zone] stateless, bouton commun réutilisé.
 */
data class ZoneActions(
    val onContinue: (ZoneSelection) -> Unit = {},
)

data class ZoneSelection(
    val search: String = "",
    val region: String = "",
    val district: String = "",
    val commune: String = "",
    val fokontany: String = "",
)

@Composable
fun ZoneScreen(
    modifier: Modifier = Modifier,
    actions: ZoneActions = ZoneActions(),
) {
    // Valeurs par défaut = capture.
    val defaultRegion = stringResource(R.string.zone_region_default)
    val defaultDistrict = stringResource(R.string.zone_district_default)
    val defaultCommune = stringResource(R.string.zone_commune_default)
    val defaultFokontany = stringResource(R.string.zone_fokontany_default)
    var search by rememberSaveable { mutableStateOf("") }
    var region by rememberSaveable { mutableStateOf(defaultRegion) }
    var district by rememberSaveable { mutableStateOf(defaultDistrict) }
    var commune by rememberSaveable { mutableStateOf(defaultCommune) }
    var fokontany by rememberSaveable { mutableStateOf(defaultFokontany) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = stringResource(R.string.zone_title_cd),
                    tint = FlagGreen,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.zone_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TextPrimary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.zone_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            ZoneSearchField(
                value = search,
                onValueChange = { search = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            ZoneMapPreview()

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ZoneDropdownField(
                    label = stringResource(R.string.zone_region_label),
                    value = region,
                    options = ZoneRegions,
                    onSelect = { region = it },
                )
                ZoneDropdownField(
                    label = stringResource(R.string.zone_district_label),
                    value = district,
                    options = ZoneDistricts,
                    onSelect = { district = it },
                )
                ZoneDropdownField(
                    label = stringResource(R.string.zone_commune_label),
                    value = commune,
                    options = ZoneCommunes,
                    onSelect = { commune = it },
                )
                ZoneDropdownField(
                    label = stringResource(R.string.zone_fokontany_label),
                    value = fokontany,
                    options = ZoneFokontanys,
                    onSelect = { fokontany = it },
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.zone_continue),
                onClick = {
                    actions.onContinue(
                        ZoneSelection(
                            search = search.trim(),
                            region = region,
                            district = district,
                            commune = commune,
                            fokontany = fokontany,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Listes de démonstration — à remplacer par le référentiel officiel. */
private val ZoneRegions = listOf(
    "Analamanga",
    "Vakinankaratra",
    "Haute Matsiatra",
    "Atsinanana",
    "Boeny",
)
private val ZoneDistricts = listOf(
    "Antananarivo Avaradrano",
    "Antananarivo Atsimondrano",
    "Antananarivo Renivohitra",
    "Antsirabe I",
)
private val ZoneCommunes = listOf(
    "Antananarivo",
    "Antsirabe",
    "Toamasina",
    "Mahajanga",
)
private val ZoneFokontanys = listOf(
    "Andohalo",
    "Analakely",
    "Isoraka",
    "Antaninarenina",
)

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun ZoneScreenPreview() {
    IrayTheme {
        ZoneScreen()
    }
}
