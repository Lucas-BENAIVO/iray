package mg.iray.app.ui.components.signalement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.DividerSubtle
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.FlagRed

/**
 * Liste radio des sous-catégories - lignes séparées, sélection bleue.
 *
 * Best practice : stateless ([selected]/[onSelect] hoistés).
 */
@Composable
fun SignalementSubcategoryRadioGroup(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BrandWhite,
        border = BorderStroke(1.dp, OutlineOnWhite),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            options.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.RadioButton) { onSelect(option) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    RadioButton(
                        selected = option == selected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = FlagRed,
                        ),
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                    )
                }
                if (index < options.lastIndex) {
                    HorizontalDivider(
                        color = DividerSubtle,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignalementSubcategoryRadioGroupPreview() {
    IrayTheme {
        SignalementSubcategoryRadioGroup(
            options = listOf("Coupure d’électricité", "Câble dangereux", "Autre"),
            selected = "Coupure d’électricité",
            onSelect = {},
        )
    }
}
