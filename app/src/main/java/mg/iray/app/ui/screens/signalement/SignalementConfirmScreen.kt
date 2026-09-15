package mg.iray.app.ui.screens.signalement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.signalement.SignalementRecapCard
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary

/**
 * Écran "Confirmation" du signalement — récap + priorité + Envoyer.
 *
 * Best practice : écran stateless, données en paramètres.
 */
data class SignalementConfirmActions(
    val onBack: () -> Unit = {},
    val onSend: () -> Unit = {},
)

@Composable
fun SignalementConfirmScreen(
    categoryId: String,
    subcategory: String,
    address: String,
    description: String,
    modifier: Modifier = Modifier,
    actions: SignalementConfirmActions = SignalementConfirmActions(),
) {
    val category = SignalementCatalog.get(categoryId)

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
                    contentDescription = stringResource(R.string.signalement_confirm_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.signalement_confirm_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
        ) {
            SignalementRecapCard(
                categoryLabel = stringResource(category.labelRes),
                categoryIcon = category.icon,
                subcategory = subcategory,
                address = address,
                description = description,
                priority = category.priority,
            )

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_send),
                onClick = actions.onSend,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementConfirmScreenPreview() {
    IrayTheme {
        SignalementConfirmScreen(
            categoryId = "electricite",
            subcategory = "Tapaka ny herinaratra",
            address = "Fokontany Andohalo, Antananarivo",
            description = "Tapaka hatramin’ny maraina",
        )
    }
}
