package mg.iray.app.ui.screens.monProfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.FlagAccentBar
import mg.iray.app.ui.components.IrayBottomBar
import mg.iray.app.ui.components.IrayBottomBarActions
import mg.iray.app.ui.components.IrayBottomTab
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.IrayScreenHeader
import mg.iray.app.ui.components.IraySecondaryButton
import mg.iray.app.ui.components.mes.MesStatusBadge
import mg.iray.app.ui.components.mes.MesStatusColors
import mg.iray.app.ui.components.profile.ProfileInfoRow
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SuccessAvatar
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Écran "Mon profil" — fiche citoyen une fois le parcours création terminé.
 *
 * Affiche les infos saisies + zone. Bottom bar onglet Profil actif.
 */
data class MonProfilActions(
    val onBack: () -> Unit = {},
    val onHome: () -> Unit = {},
    val onDemarches: () -> Unit = {},
    val onSignalements: () -> Unit = {},
    val onEditProfile: () -> Unit = {},
    val onEditZone: () -> Unit = {},
)

@Composable
fun MonProfilScreen(
    firstName: String,
    lastName: String,
    phone: String,
    birthdate: String,
    commune: String,
    fokontany: String,
    modifier: Modifier = Modifier,
    actions: MonProfilActions = MonProfilActions(),
) {
    val empty = stringResource(R.string.mon_profil_empty_value)
    val fullName = listOf(firstName, lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { empty }
    val zoneLabel = listOf(commune, fokontany)
        .filter { it.isNotBlank() }
        .joinToString(", ")
        .ifBlank { empty }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .statusBarsPadding(),
    ) {
        IrayScreenHeader(
            title = stringResource(R.string.mon_profil_title),
            onBack = actions.onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = CircleShape,
                color = SuccessAvatar.copy(alpha = 0.12f),
                modifier = Modifier.size(88.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.success_avatar_cd),
                        tint = SuccessAvatar,
                        modifier = Modifier.size(44.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = fullName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                ),
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlagAccentBar(
                modifier = Modifier.fillMaxWidth(0.2f),
                height = 3.dp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            MesStatusBadge(
                label = stringResource(R.string.mon_profil_citizen_badge),
                tint = MesStatusColors.Done,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = zoneLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ProfileInfoRow(
                    label = stringResource(R.string.profile_first_name_label)
                        .removeSuffix(" *"),
                    value = firstName.ifBlank { empty },
                    icon = Icons.Filled.Person,
                )
                ProfileInfoRow(
                    label = stringResource(R.string.profile_last_name_label)
                        .removeSuffix(" *"),
                    value = lastName.ifBlank { empty },
                    icon = Icons.Filled.Person,
                )
                ProfileInfoRow(
                    label = stringResource(R.string.profile_phone_label)
                        .removeSuffix(" *"),
                    value = phone.ifBlank { empty },
                    icon = Icons.Filled.Phone,
                )
                ProfileInfoRow(
                    label = stringResource(R.string.profile_birthdate_label),
                    value = birthdate.ifBlank { empty },
                    icon = Icons.Filled.DateRange,
                )
                ProfileInfoRow(
                    label = stringResource(R.string.mon_profil_zone_label),
                    value = zoneLabel,
                    icon = Icons.Filled.LocationOn,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.mon_profil_edit),
                onClick = actions.onEditProfile,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            IraySecondaryButton(
                label = stringResource(R.string.mon_profil_edit_zone),
                onClick = actions.onEditZone,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        IrayBottomBar(
            selected = IrayBottomTab.Profile,
            actions = IrayBottomBarActions(
                onHome = actions.onHome,
                onDemarches = actions.onDemarches,
                onSignalements = actions.onSignalements,
                onProfile = {},
            ),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun MonProfilScreenPreview() {
    IrayTheme {
        MonProfilScreen(
            firstName = "Jean",
            lastName = "Rakoto",
            phone = "+261 34 12 34 567",
            birthdate = "12/03/1995",
            commune = "Antananarivo",
            fokontany = "Andohalo",
        )
    }
}
