package mg.iray.app.ui.components.upload

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Zone d’upload pointillée — invite galerie/fichiers ou résumé du fichier choisi.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun UploadDropZone(
    fileName: String?,
    fileSizeLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        color = SurfaceFeatured,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .clickable(role = Role.Button, onClick = onClick),
        ) {
            // Bordure pointillée dessinée par-dessus.
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.dp.toPx()
                val inset = strokeWidth / 2f
                drawRoundRect(
                    color = OutlineOnWhite,
                    topLeft = Offset(inset, inset),
                    size = Size(
                        size.width - strokeWidth,
                        size.height - strokeWidth,
                    ),
                    cornerRadius = CornerRadius(20.dp.toPx()),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
                    ),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp),
            ) {
                if (fileName == null) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = null,
                        tint = ZoneMapPin,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.upload_drop_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.documents_types),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = null,
                        tint = BrandAccent,
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    if (fileSizeLabel.isNotEmpty()) {
                        Text(
                            text = fileSizeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadDropZonePreview() {
    IrayTheme {
        UploadDropZone(fileName = null, fileSizeLabel = "", onClick = {})
    }
}
