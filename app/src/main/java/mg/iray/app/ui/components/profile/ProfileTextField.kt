package mg.iray.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Champ du formulaire profil - icône + libellé + zone de saisie.
 * [error] affiché sous le champ quand non null (validation).
 */
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    accentColor: Color = BrandAccent,
    error: String? = null,
) {
    val hasError = !error.isNullOrBlank()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = if (hasError) FlagRed else accentColor,
            modifier = Modifier
                .paddingTopForIcon()
                .size(22.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = hint,
                        color = TextSecondary.copy(alpha = 0.7f),
                    )
                },
                singleLine = singleLine,
                isError = hasError,
                supportingText = if (hasError) {
                    {
                        Text(
                            text = error.orEmpty(),
                            color = FlagRed,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = OutlineOnWhite,
                    errorBorderColor = FlagRed,
                    errorSupportingTextColor = FlagRed,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun Modifier.paddingTopForIcon(): Modifier = padding(top = 28.dp)

@Preview(showBackground = true)
@Composable
private fun ProfileTextFieldPreview() {
    IrayTheme {
        ProfileTextField(
            value = "",
            onValueChange = {},
            label = "Prénom *",
            hint = "Jean",
            leadingIcon = Icons.Filled.Person,
        )
    }
}
