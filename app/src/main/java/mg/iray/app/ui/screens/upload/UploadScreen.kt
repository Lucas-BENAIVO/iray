package mg.iray.app.ui.screens.upload

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.components.IrayPrimaryButton
import mg.iray.app.ui.components.upload.UploadDropZone
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfacePage
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.ZoneMapPin

/**
 * Page d’upload d’un document — sélecteur système (PDF/JPG/PNG) + "Joindre".
 *
 * Best practice : URI → nom/taille via ContentResolver, résultat remonté
 * par [UploadActions.onFileConfirmed], aucun texte en dur.
 */
data class UploadActions(
    val onBack: () -> Unit = {},
    val onFileConfirmed: (fileName: String) -> Unit = {},
)

@Composable
fun UploadScreen(
    documentName: String,
    modifier: Modifier = Modifier,
    actions: UploadActions = UploadActions(),
) {
    val context = LocalContext.current
    var fileName by rememberSaveable { mutableStateOf<String?>(null) }
    var fileSizeLabel by rememberSaveable { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            val (name, size) = queryDisplayNameAndSize(
                contentResolver = context.contentResolver,
                uri = uri,
            )
            fileName = name
            fileSizeLabel = size
        }
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
                    contentDescription = stringResource(R.string.upload_back_cd),
                    tint = TextPrimary,
                )
            }
            Text(
                text = documentName,
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
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            UploadDropZone(
                fileName = fileName,
                fileSizeLabel = fileSizeLabel,
                onClick = {
                    picker.launch(
                        arrayOf("application/pdf", "image/jpeg", "image/png"),
                    )
                },
            )

            if (fileName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        fileName = null
                        fileSizeLabel = ""
                    },
                ) {
                    Text(
                        text = stringResource(R.string.upload_remove_cd),
                        color = ZoneMapPin,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IrayPrimaryButton(
                label = stringResource(R.string.upload_cta),
                onClick = { fileName?.let(actions.onFileConfirmed) },
                enabled = fileName != null,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun queryDisplayNameAndSize(
    contentResolver: ContentResolver,
    uri: Uri,
): Pair<String, String> {
    contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0) ?: uri.lastPathSegment.orEmpty()
            val sizeBytes = cursor.getLong(1)
            return name to formatSize(sizeBytes)
        }
    }
    return (uri.lastPathSegment ?: "fichier") to ""
}

private fun formatSize(sizeBytes: Long): String {
    if (sizeBytes <= 0) return ""
    val kb = sizeBytes / 1024.0
    return if (kb < 1024) {
        "${kb.toInt()} Ko"
    } else {
        "%.1f Mo".format(kb / 1024.0)
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_7")
@Composable
private fun UploadScreenPreview() {
    IrayTheme {
        UploadScreen(documentName = "Pièce d’identité (CIN ou passeport)")
    }
}
