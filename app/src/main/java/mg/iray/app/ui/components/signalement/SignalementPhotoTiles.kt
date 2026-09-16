package mg.iray.app.ui.components.signalement

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.FlagRed

/**
 * Vignettes photos + tuile "+" - aperçu réel via ImageView système.
 *
 * Best practice : stateless (URIs en paramètres), pas de dépendance lourde.
 */
@Composable
fun SignalementPhotoTiles(
    photoUris: List<String>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.signalement_add_photo_label),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )

        Spacer(
            modifier = Modifier.size(8.dp),
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(photoUris, key = { it }) { uri ->
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    update = { view -> view.setImageURI(Uri.parse(uri)) },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceFeatured,
                    border = BorderStroke(1.dp, OutlineOnWhite),
                    modifier = Modifier.size(72.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.signalement_add_photo_cd),
                            onClick = onAddClick,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(
                                R.string.signalement_add_photo_cd,
                            ),
                            tint = FlagRed,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignalementPhotoTilesPreview() {
    IrayTheme {
        Surface(color = BrandWhite) {
            SignalementPhotoTiles(photoUris = emptyList(), onAddClick = {})
        }
    }
}
