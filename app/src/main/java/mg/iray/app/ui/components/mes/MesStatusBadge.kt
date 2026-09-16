package mg.iray.app.ui.components.mes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.PriorityOrange
import mg.iray.app.ui.theme.SuccessAvatar

/**
 * Pastille de statut - couleur + libellé (stateless).
 */
@Composable
fun MesStatusBadge(
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = tint.copy(alpha = 0.14f),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = tint,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

object MesStatusColors {
    val Received = SuccessAvatar
    val Ongoing = PriorityOrange
    val Done = FlagGreen
}

@Preview(showBackground = true)
@Composable
private fun MesStatusBadgePreview() {
    IrayTheme {
        MesStatusBadge(label = "En cours", tint = MesStatusColors.Ongoing)
    }
}
