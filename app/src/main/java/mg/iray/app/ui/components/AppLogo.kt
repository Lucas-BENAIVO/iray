package mg.iray.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayTheme

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    val description = stringResource(R.string.welcome_content_desc_logo)

    if (content != null) {
        content()
    } else {
        Image(
            painter = painterResource(R.drawable.logo_republique),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .height(72.dp)
                .widthIn(max = 220.dp)
                .semantics { contentDescription = description }
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
