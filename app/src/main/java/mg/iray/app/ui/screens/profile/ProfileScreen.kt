package mg.iray.app.ui.screens.profile

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.profile.ProfileAvatarPicker
import mg.iray.app.ui.components.profile.ProfileTextField
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Créons votre profil" — copie la capture du 11/09 13:42.
 *
 * Best practice : état du formulaire hoisté ici (rememberSaveable),
 * composants de [mg.iray.app.ui.components.profile] stateless,
 * bouton commun [IrayPrimaryButton] réutilisé.
 */
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
    actions: ProfileActions = ProfileActions(),
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var birthdate by rememberSaveable { mutableStateOf("") }

    // Champs obligatoires (*) remplis → CTA actif, sinon gris désactivé.
    val isValid = firstName.isNotBlank() &&
        lastName.isNotBlank() &&
        phone.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProfileTopBar(
            onBackClick = actions.onBack,
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
            Text(
                text = stringResource(R.string.profile_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileAvatarPicker(
                onClick = actions.onAvatarClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                ProfileTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = stringResource(R.string.profile_first_name_label),
                    hint = stringResource(R.string.profile_first_name_hint),
                    leadingIcon = Icons.Filled.Person,
                )
                ProfileTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = stringResource(R.string.profile_last_name_label),
                    hint = stringResource(R.string.profile_last_name_hint),
                    leadingIcon = Icons.Filled.Person,
                )
                ProfileTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = stringResource(R.string.profile_phone_label),
                    hint = stringResource(R.string.profile_phone_hint),
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone,
                )
                ProfileTextField(
                    value = birthdate,
                    onValueChange = { birthdate = it },
                    label = stringResource(R.string.profile_birthdate_label),
                    hint = stringResource(R.string.profile_birthdate_hint),
                    leadingIcon = Icons.Filled.DateRange,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.profile_continue),
                onClick = {
                    actions.onContinue(
                        ProfileForm(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            phone = phone.trim(),
                            birthdate = birthdate.trim(),
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(start = 8.dp, end = 24.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.profile_back_cd),
                tint = TextPrimary,
            )
        }
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun ProfileScreenPreview() {
    IrayTheme {
        ProfileScreen()
    }
}
