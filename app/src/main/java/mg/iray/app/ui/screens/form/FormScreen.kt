package mg.iray.app.ui.screens.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.components.zone.ZoneDropdownField
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Informations personnelles" (étape 4 sur 5) — UN écran dynamique.
 *
 * Champs pré-remplis depuis le profil ([initialFirstName]…), "Lieu de
 * naissance" affiché seulement si [asksBirthPlace] (cas CIN).
 * Best practice : état hoisté ici, champs partagés stateless réutilisés.
 */
data class FormActions(
    val onBack: () -> Unit = {},
    val onSubmit: (FormData) -> Unit = {},
)

data class FormData(
    val lastName: String = "",
    val firstName: String = "",
    val birthdate: String = "",
    val address: String = "",
    val birthPlace: String = "",
)

@Composable
fun FormScreen(
    initialLastName: String,
    initialFirstName: String,
    initialBirthdate: String,
    initialAddress: String,
    asksBirthPlace: Boolean,
    modifier: Modifier = Modifier,
    actions: FormActions = FormActions(),
) {
    var lastName by rememberSaveable { mutableStateOf(initialLastName) }
    var firstName by rememberSaveable { mutableStateOf(initialFirstName) }
    var birthdate by rememberSaveable { mutableStateOf(initialBirthdate) }
    var address by rememberSaveable { mutableStateOf(initialAddress) }
    var birthPlace by rememberSaveable { mutableStateOf("") }

    // Champs obligatoires (*) remplis → CTA actif.
    val isValid = firstName.isNotBlank() &&
        birthdate.isNotBlank() &&
        address.isNotBlank() &&
        (!asksBirthPlace || birthPlace.isNotBlank())

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
                    contentDescription = stringResource(R.string.form_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.form_step),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.form_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                ProfileTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = stringResource(R.string.form_last_name_label),
                    hint = stringResource(R.string.profile_last_name_hint),
                    leadingIcon = Icons.Filled.Person,
                )
                ProfileTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = stringResource(R.string.profile_first_name_label),
                    hint = stringResource(R.string.profile_first_name_hint),
                    leadingIcon = Icons.Filled.Person,
                )
                ProfileTextField(
                    value = birthdate,
                    onValueChange = { birthdate = it },
                    label = stringResource(R.string.form_birthdate_label),
                    hint = stringResource(R.string.profile_birthdate_hint),
                    leadingIcon = Icons.Filled.DateRange,
                )
                ProfileTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(R.string.form_address_label),
                    hint = stringResource(R.string.form_address_hint),
                    leadingIcon = Icons.Filled.Home,
                )
                if (asksBirthPlace) {
                    ZoneDropdownField(
                        label = stringResource(R.string.form_birthplace_label),
                        value = birthPlace,
                        options = BirthPlaces,
                        onSelect = { birthPlace = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.form_submit),
                onClick = {
                    actions.onSubmit(
                        FormData(
                            lastName = lastName.trim(),
                            firstName = firstName.trim(),
                            birthdate = birthdate.trim(),
                            address = address.trim(),
                            birthPlace = birthPlace.trim(),
                        ),
                    )
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Lieux de naissance proposés — à remplacer par le référentiel officiel. */
private val BirthPlaces = listOf(
    "Antananarivo",
    "Antsirabe",
    "Toamasina",
    "Mahajanga",
    "Fianarantsoa",
)

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun FormScreenPreview() {
    IrayTheme {
        FormScreen(
            initialLastName = "Rakoto",
            initialFirstName = "Jean",
            initialBirthdate = "12/05/1990",
            initialAddress = "Andohalo",
            asksBirthPlace = false,
        )
    }
}
