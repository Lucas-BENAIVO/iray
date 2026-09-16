package mg.iray.app.ui.components.documents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.irayFastClick
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagGreen
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Carte document à joindre - même effet clic / flèche que les autres CTA.
 */
@Composable
fun DocumentUploadRow(
    documentName: String,
    attachedFileName: String?,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = OnboardingInk.copy(alpha = 0.14f),
                spotColor = FlagGreen.copy(alpha = 0.16f),
            )
            .clip(shape)
            .background(BrandWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        FlagGreen.copy(alpha = 0.22f),
                        FlagGreen.copy(alpha = 0.06f),
                    ),
                ),
                shape = shape,
            )
            .irayFastClick(onClick = onAttachClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                FlagGreen.copy(alpha = 0.12f),
                                FlagGreen.copy(alpha = 0.20f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = FlagGreen,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documentName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = IrayDisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    ),
                    color = TextPrimary,
                )
                Text(
                    text = attachedFileName
                        ?: stringResource(R.string.documents_attach),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = IrayFontFamily,
                        fontWeight = if (attachedFileName != null) {
                            FontWeight.Medium
                        } else {
                            FontWeight.Normal
                        },
                    ),
                    color = if (attachedFileName != null) FlagGreen else TextSecondary,
                )
                Text(
                    text = stringResource(R.string.documents_types),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.8f),
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FlagGreen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.documents_row_cd),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F7F5)
@Composable
private fun DocumentUploadRowPreview() {
    IrayTheme {
        DocumentUploadRow(
            documentName = "Pièce d’identité (CIN ou passeport)",
            attachedFileName = null,
            onAttachClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
