package mg.iray.app.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.SuccessAvatar
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Ligne d’info profil — icône + libellé + valeur.
 */
@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = BrandWhite,
        border = BorderStroke(1.dp, OutlineOnWhite),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = SuccessAvatar.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SuccessAvatar,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = TextPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileInfoRowPreview() {
    IrayTheme {
        ProfileInfoRow(
            label = "Téléphone",
            value = "+261 34 12 34 567",
            icon = Icons.Filled.Person,
            modifier = Modifier.padding(16.dp),
        )
    }
}
