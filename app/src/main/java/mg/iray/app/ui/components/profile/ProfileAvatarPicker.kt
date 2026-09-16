package mg.iray.app.ui.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextSecondary

/**
 * Pastille avatar - cercle bleu-gris clair + icône appareil photo.
 *
 * Best practice : stateless, [onClick] hoisté, aucun texte en dur.
 */
@Composable
fun ProfileAvatarPicker(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(88.dp),
        shape = CircleShape,
        color = SurfaceFeatured,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.profile_avatar_cd),
                    onClick = onClick,
                ),
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = stringResource(R.string.profile_avatar_cd),
                tint = TextSecondary,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarPickerPreview() {
    IrayTheme {
        ProfileAvatarPicker(onClick = {})
    }
}