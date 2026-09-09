package mg.iray.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.BrandBlue
import mg.iray.app.ui.theme.BrandRed
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.TextSecondary

/**
 * Logo slot for the welcome header.
 * Pass a real [content] (e.g. Image(painterResource(R.drawable.logo))) when the asset is ready.
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    val description = stringResource(R.string.welcome_content_desc_logo)

    Box(
        modifier = modifier.semantics { contentDescription = description },
        contentAlignment = Alignment.CenterStart
    ) {
        if (content != null) {
            content()
        } else {
            LogoPlaceholder()
        }
    }
}

@Composable
private fun LogoPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 44.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, BrandBlue.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                .background(BrandWhite),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandBlue)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(11.dp)
                        .height(22.dp)
                        .background(BrandRed)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "GOUVERNEMENT",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontSize = 10.sp
            ),
            color = BrandBlue
        )
        Text(
            text = "Liberté · Égalité · Fraternité",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.sp),
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppLogoPreview() {
    IrayTheme {
        AppLogo()
    }
}
