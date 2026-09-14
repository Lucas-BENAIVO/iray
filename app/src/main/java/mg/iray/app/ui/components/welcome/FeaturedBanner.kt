package mg.iray.app.ui.components.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.theme.IrayDisplayFontFamily
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.SurfaceFeatured
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary

@Composable
fun FeaturedBanner(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = SurfaceFeatured,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = IrayDisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = IrayFontFamily,
                    lineHeight = 16.sp,
                ),
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeaturedBannerPreview() {
    IrayTheme {
        FeaturedBanner(
            title = stringResource(R.string.welcome_featured_title),
            body = stringResource(R.string.welcome_featured_body),
        )
    }
}
