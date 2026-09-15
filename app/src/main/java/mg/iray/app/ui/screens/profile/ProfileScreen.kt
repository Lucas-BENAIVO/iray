package mg.iray.app.ui.screens.profile

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.profile.ProfileAvatarPicker
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.validation.FormValidators

data class ProfileActions(
    val onBack: () -> Unit = {},
    val onAvatarClick: () -> Unit = {},
    val onContinue: (ProfileForm) -> Unit = {},
)

data class ProfileForm(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val birthdate: String = "",
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    initialFirstName: String = "",
    initialLastName: String = "",
    initialPhone: String = "",
    initialBirthdate: String = "",
    actions: ProfileActions = ProfileActions(),
) {
    var firstName by rememberSaveable(initialFirstName) { mutableStateOf(initialFirstName) }
    var lastName by rememberSaveable(initialLastName) { mutableStateOf(initialLastName) }
    var phone by rememberSaveable(initialPhone) { mutableStateOf(initialPhone) }
    var birthdate by rememberSaveable(initialBirthdate) { mutableStateOf(initialBirthdate) }
    var attemptedSubmit by rememberSaveable { mutableStateOf(false) }

    val firstNameOk = FormValidators.isValidName(firstName)
    val lastNameOk = FormValidators.isValidName(lastName)
    val phoneOk = FormValidators.isValidPhone(phone)
    val birthdateOk = FormValidators.isValidBirthdate(birthdate)
    val isValid = firstNameOk && lastNameOk && phoneOk && birthdateOk

    val nameError = stringResource(R.string.validation_name_error)
    val phoneError = stringResource(R.string.validation_phone_error)
    val birthdateError = stringResource(R.string.validation_birthdate_error)

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
            title = stringResource(R.string.profile_title),
            subtitle = stringResource(R.string.profile_subtitle),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            ProfileAvatarPicker(
                onClick = actions.onAvatarClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
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
                ProfileTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = stringResource(R.string.profile_last_name_label),
                    hint = stringResource(R.string.profile_last_name_hint),
                    leadingIcon = Icons.Filled.Person,
                    error = nameError.takeIf {
                        showError(lastName.isNotBlank(), lastNameOk)
                    },
                )
                ProfileTextField(
                    value = phone,
                    onValueChange = { phone = FormValidators.formatPhoneInput(it) },
                    label = stringResource(R.string.profile_phone_label),
                    hint = stringResource(R.string.profile_phone_hint),
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone,
                    error = phoneError.takeIf {
                        showError(phone.isNotBlank(), phoneOk)
                    },
                )
                ProfileTextField(
                    value = birthdate,
                    onValueChange = { birthdate = FormValidators.formatBirthdateInput(it) },
                    label = stringResource(R.string.profile_birthdate_label),
                    hint = stringResource(R.string.profile_birthdate_hint),
                    leadingIcon = Icons.Filled.DateRange,
                    keyboardType = KeyboardType.Number,
                    error = birthdateError.takeIf {
                        showError(birthdate.isNotBlank(), birthdateOk)
                    },
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.profile_continue),
                onClick = {
                    attemptedSubmit = true
                    if (!isValid) return@IrayPrimaryButton
                    actions.onContinue(
                        ProfileForm(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            phone = FormValidators.normalizePhone(phone),
                            birthdate = birthdate.trim(),
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid,
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun ProfileScreenPreview() {
    IrayTheme {
        ProfileScreen()
    }
}
