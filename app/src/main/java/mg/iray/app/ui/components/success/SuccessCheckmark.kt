package mg.iray.app.ui.components.success

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SuccessHalo
import mg.iray.app.ui.theme.TextOnBrand

/**
 * Check de succès — halo vert clair + confettis + pastille verte + check blanc.
 *
 * Copie la capture "Profil créé !". Confettis fixes (pur décoratif).
 */
@Composable
fun SuccessCheckmark(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(148.dp),
    ) {
        // Confettis fixes autour du halo.
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val dots = listOf(
                Triple(0.12f, 0.30f, 3.5f),
                Triple(0.20f, 0.12f, 2.5f),
                Triple(0.38f, 0.05f, 3.0f),
                Triple(0.62f, 0.05f, 2.5f),
                Triple(0.80f, 0.12f, 3.5f),
                Triple(0.90f, 0.30f, 2.5f),
                Triple(0.08f, 0.55f, 2.5f),
                Triple(0.93f, 0.55f, 3.0f),
                Triple(0.15f, 0.78f, 3.0f),
                Triple(0.85f, 0.78f, 2.5f),
            )
            dots.forEach { (fx, fy, r) ->
                drawCircle(
                    color = FlagGreen,
                    radius = r * density,
                    center = Offset(w * fx, h * fy),
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = SuccessHalo,
            modifier = Modifier.size(116.dp),
        ) {}

        Surface(
            shape = CircleShape,
            color = FlagGreen,
            modifier = Modifier.size(72.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.success_check_cd),
                    tint = TextOnBrand,
                    modifier = Modifier.size(38.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessCheckmarkPreview() {
    IrayTheme {
        SuccessCheckmark()
    }
}
