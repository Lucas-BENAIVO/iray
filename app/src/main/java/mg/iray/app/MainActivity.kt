package mg.iray.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.welcome.WelcomeActions
import mg.iray.app.ui.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IrayTheme {
                WelcomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    actions = WelcomeActions(
                        onAskQuestion = { /* TODO: navigate */ },
                        onFollowAnswers = { /* TODO: navigate */ },
                        onConsultations = { /* TODO: navigate */ },
                        onFeaturedCta = { /* TODO: navigate */ }
                    )
                )
            }
        }
    }
}
