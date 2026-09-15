package mg.iray.app.ui.components.mes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.irayFastClick
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.DividerSubtle
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OnboardingInk
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Carte historique (démarche / signalement) — ombre, accent, statut, n° dossier.
 */
@Composable
fun MesRequestCard(
    title: String,
    subtitle: String,
    dossierLabel: String,
    date: String,
    statusLabel: String,
    statusTint: Color,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = OnboardingInk.copy(alpha = 0.14f),
                spotColor = iconTint.copy(alpha = 0.18f),
            )
            .clip(shape)
            .background(BrandWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        iconTint.copy(alpha = 0.28f),
                        iconTint.copy(alpha = 0.06f),
                    ),
                ),
                shape = shape,
            )
            .irayFastClick(onClick = onClick)
            .padding(18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    iconTint.copy(alpha = 0.12f),
                                    iconTint.copy(alpha = 0.20f),
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = IrayDisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.1).sp,
                        ),
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = IrayFontFamily,
                        ),
                        color = TextSecondary,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconTint),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = DividerSubtle)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = dossierLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = IrayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = iconTint,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = IrayFontFamily,
                        ),
                        color = TextSecondary.copy(alpha = 0.85f),
                    )
                }
                MesStatusBadge(label = statusLabel, tint = statusTint)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F7F5)
@Composable
private fun MesRequestCardPreview() {
    IrayTheme {
        MesRequestCard(
            title = "Certificat de résidence",
            subtitle = "Mairie — Antananarivo",
            dossierLabel = stringResource(R.string.mes_demarches_dossier_format, "MAD-2026-184221"),
            date = "12 sept. 2026",
            statusLabel = "En cours",
            statusTint = MesStatusColors.Ongoing,
            icon = Icons.Filled.Description,
            iconTint = MesStatusColors.Done,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
