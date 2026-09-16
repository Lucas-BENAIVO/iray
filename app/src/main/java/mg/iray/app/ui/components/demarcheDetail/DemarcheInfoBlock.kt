package mg.iray.app.ui.components.demarcheDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

/**
 * Bloc "Informations" - délai estimé + frais, icônes vertes.
 *
 * Best practice : stateless, aucun texte en dur.
 */
@Composable
fun DemarcheInfoBlock(
    delay: String,
    fees: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.demarche_detail_info_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoLine(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.size(20.dp),
                )
            },
            label = stringResource(R.string.demarche_detail_delay_label),
            value = delay,
        )

        Spacer(modifier = Modifier.height(6.dp))

        InfoLine(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.size(20.dp),
                )
            },
            label = stringResource(R.string.demarche_detail_fees_label),
            value = fees,
        )
    }
}

@Composable
private fun InfoLine(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        icon()
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextSecondary)) {
                    append(label)
                    append(" ")
                }
                withStyle(
                    SpanStyle(
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                    ),
                ) {
                    append(value)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DemarcheInfoBlockPreview() {
    IrayTheme {
        DemarcheInfoBlock(
            delay = "2 à 7 jours ouvrés",
            fees = "Gratuit ou selon la commune",
        )
    }
}
