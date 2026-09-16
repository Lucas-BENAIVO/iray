package mg.iray.app.ui.screens.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.profile.BirthdatePickerField
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.components.zone.ZoneDropdownField
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.validation.FormValidators

/**
 * Écran "Informations personnelles" (étape 4 sur 5) - UN écran dynamique.
 *
 * Champs pré-remplis depuis le profil ([initialFirstName]…), "Lieu de
 * naissance" affiché seulement si [asksBirthPlace] (cas CIN).
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
    var attemptedSubmit by rememberSaveable { mutableStateOf(false) }

    val firstNameOk = FormValidators.isValidName(firstName)
    val lastNameOk = lastName.isBlank() || FormValidators.isValidName(lastName)
    val birthdateOk = FormValidators.isValidBirthdate(birthdate)
    val addressOk = address.trim().length >= 3
    val birthPlaceOk = !asksBirthPlace || birthPlace.isNotBlank()
    val isValid = firstNameOk && lastNameOk && birthdateOk && addressOk && birthPlaceOk

    val nameError = stringResource(R.string.validation_name_error)
    val birthdateError = stringResource(R.string.validation_birthdate_error)
    val requiredError = stringResource(R.string.validation_required_error)

    fun showError(hasContent: Boolean, ok: Boolean): Boolean =
        (attemptedSubmit || hasContent) && !ok

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.form_title),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                ProfileTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = stringResource(R.string.form_last_name_label),
                    hint = stringResource(R.string.profile_last_name_hint),
                    leadingIcon = Icons.Filled.Person,
                    error = nameError.takeIf {
                        showError(lastName.isNotBlank(), lastNameOk)
                    },
                )
                ProfileTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = stringResource(R.string.profile_first_name_label),
                    hint = stringResource(R.string.profile_first_name_hint),
                    leadingIcon = Icons.Filled.Person,
                    error = nameError.takeIf {
                        showError(firstName.isNotBlank(), firstNameOk)
                    },
                )
                BirthdatePickerField(
                    value = birthdate,
                    onValueChange = { birthdate = it },
                    label = stringResource(R.string.form_birthdate_label),
                    hint = stringResource(R.string.profile_birthdate_hint),
                    error = birthdateError.takeIf {
                        showError(birthdate.isNotBlank(), birthdateOk)
                    },
                )
                ProfileTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(R.string.form_address_label),
                    hint = stringResource(R.string.form_address_hint),
                    leadingIcon = Icons.Filled.Home,
                    error = requiredError.takeIf {
                        showError(address.isNotBlank(), addressOk)
                    },
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
                    attemptedSubmit = true
                    if (!isValid) return@IrayPrimaryButton
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

/** Lieux de naissance proposés - à remplacer par le référentiel officiel. */
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
