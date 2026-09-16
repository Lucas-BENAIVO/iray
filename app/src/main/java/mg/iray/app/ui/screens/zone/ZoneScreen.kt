package mg.iray.app.ui.screens.zone

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mg.iray.app.R
import mg.iray.app.entity.TerritoryEntity
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.zone.ZoneDropdownField
import mg.iray.app.ui.controller.ZoneController
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextSecondary

data class ZoneActions(
    val onContinue: (ZoneSelection) -> Unit = {},
    val onBack: (() -> Unit)? = null,
)

data class ZoneSelection(
    val region: String = "",
    val district: String = "",
    val commune: String = "",
    val arrondissement: String = "",
    val fokontany: String = "",
    val regionId: String = "",
    val districtId: String = "",
    val communeId: String = "",
    val arrondissementId: String = "",
    val fokontanyId: String = "",
)

@Composable
fun ZoneScreen(
    modifier: Modifier = Modifier,
    actions: ZoneActions = ZoneActions(),
    controller: ZoneController = viewModel(factory = ZoneController.Factory),
) {
    val selection by controller.selection.collectAsStateWithLifecycle()
    val regions by controller.regions.collectAsStateWithLifecycle()
    val districts by controller.districts.collectAsStateWithLifecycle()
    val communes by controller.communes.collectAsStateWithLifecycle()
    val arrondissements by controller.arrondissements.collectAsStateWithLifecycle()
    val showArrondissement by controller.showArrondissement.collectAsStateWithLifecycle()
    val fokontanys by controller.fokontanys.collectAsStateWithLifecycle()
    val canContinue by controller.canContinue.collectAsStateWithLifecycle()
    val loading by controller.loading.collectAsStateWithLifecycle()

    val placeholder = stringResource(R.string.zone_select_hint)

    // Ne jamais piéger l’utilisateur sur Faritrao (création profil).
    BackHandler(enabled = actions.onBack != null) {
        actions.onBack?.invoke()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.zone_title),
            subtitle = stringResource(R.string.zone_subtitle),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (loading && regions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = BrandAccent,
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.zone_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            if (!loading && regions.isEmpty()) {
                Text(
                    text = stringResource(R.string.zone_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                TextButton(onClick = controller::refresh) {
                    Text(text = stringResource(R.string.zone_retry))
                }
            }

            TerritoryDropdown(
                label = stringResource(R.string.zone_region_label),
                items = regions,
                selectedId = selection.regionId,
                placeholder = placeholder,
                enabled = regions.isNotEmpty(),
                onSelectId = controller::selectRegion,
            )
            TerritoryDropdown(
                label = stringResource(R.string.zone_district_label),
                items = districts,
                selectedId = selection.districtId,
                placeholder = placeholder,
                enabled = selection.regionId != null && districts.isNotEmpty(),
                onSelectId = controller::selectDistrict,
            )
            TerritoryDropdown(
                label = stringResource(R.string.zone_commune_label),
                items = communes,
                selectedId = selection.communeId,
                placeholder = placeholder,
                enabled = selection.districtId != null && communes.isNotEmpty(),
                onSelectId = controller::selectCommune,
            )
            if (showArrondissement) {
                TerritoryDropdown(
                    label = stringResource(R.string.zone_arrondissement_label),
                    items = arrondissements,
                    selectedId = selection.arrondissementId,
                    placeholder = placeholder,
                    enabled = selection.communeId != null && arrondissements.isNotEmpty(),
                    onSelectId = controller::selectArrondissement,
                )
            }
            TerritoryDropdown(
                label = stringResource(R.string.zone_fokontany_label),
                items = fokontanys,
                selectedId = selection.fokontanyId,
                placeholder = placeholder,
                enabled = selection.communeId != null &&
                    (!showArrondissement || selection.arrondissementId != null) &&
                    fokontanys.isNotEmpty(),
                onSelectId = controller::selectFokontany,
            )

            Spacer(modifier = Modifier.height(12.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.zone_continue),
                enabled = canContinue,
                onClick = {
                    val regionName = controller.nameOf(regions, selection.regionId)
                    val districtName = controller.nameOf(districts, selection.districtId)
                    val communeName = controller.nameOf(communes, selection.communeId)
                    val arrName = controller.nameOf(arrondissements, selection.arrondissementId)
                    val fokontanyName = controller.nameOf(fokontanys, selection.fokontanyId)
                    actions.onContinue(
                        ZoneSelection(
                            region = regionName,
                            district = districtName,
                            commune = communeName,
                            arrondissement = arrName,
                            fokontany = fokontanyName,
                            regionId = selection.regionId.orEmpty(),
                            districtId = selection.districtId.orEmpty(),
                            communeId = selection.communeId.orEmpty(),
                            arrondissementId = selection.arrondissementId.orEmpty(),
                            fokontanyId = selection.fokontanyId.orEmpty(),
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TerritoryDropdown(
    label: String,
    items: List<TerritoryEntity>,
    selectedId: String?,
    placeholder: String,
    enabled: Boolean,
    onSelectId: (String) -> Unit,
) {
    val value = items.firstOrNull { it.id == selectedId }?.name.orEmpty()
    val options = items.map { it.name }
    ZoneDropdownField(
        label = label,
        value = value,
        options = options,
        enabled = enabled,
        placeholder = placeholder,
        onSelect = { name ->
            items.firstOrNull { it.name == name }?.id?.let(onSelectId)
        },
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun ZoneScreenPreview() {
    IrayTheme {
        // Preview sans ViewModel Room.
    }
}
