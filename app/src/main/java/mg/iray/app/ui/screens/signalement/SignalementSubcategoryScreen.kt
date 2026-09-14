package mg.iray.app.ui.screens.signalement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import mg.iray.app.ui.components.signalement.SignalementSubcategoryRadioGroup
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.FlagGreen

/**
 * Écran sous-catégorie — UN écran dynamique par catégorie.
 *
 * Contenu fourni par [SignalementCatalog] selon [categoryId].
 * Best practice : sélection hoistée ici, groupe radio stateless.
 */
data class SignalementSubcategoryActions(
    val onBack: () -> Unit = {},
    val onContinue: (categoryId: String, subcategory: String) -> Unit = { _, _ -> },
)

@Composable
fun SignalementSubcategoryScreen(
    categoryId: String,
    modifier: Modifier = Modifier,
    actions: SignalementSubcategoryActions = SignalementSubcategoryActions(),
) {
    val category = SignalementCatalog.get(categoryId)
    var selected by rememberSaveable {
        mutableStateOf(category.subcategories.first())
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
                    contentDescription = stringResource(R.string.signalement_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = stringResource(R.string.signalement_sub_step),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = CircleShape,
                color = FlagGreen.copy(alpha = 0.14f),
                modifier = Modifier.size(64.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = FlagGreen,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(category.labelRes),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            SignalementSubcategoryRadioGroup(
                options = category.subcategories,
                selected = selected,
                onSelect = { selected = it },
            )

            Spacer(modifier = Modifier.height(28.dp))

            IrayPrimaryButton(
                label = stringResource(R.string.signalement_continue),
                onClick = { actions.onContinue(category.id, selected) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun SignalementSubcategoryScreenPreview() {
    IrayTheme {
        SignalementSubcategoryScreen(categoryId = "electricite")
    }
}
