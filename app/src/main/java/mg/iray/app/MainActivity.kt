package mg.iray.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import mg.iray.app.ui.navigation.IrayNavHost
import mg.iray.app.ui.theme.IrayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IrayTheme {
                IrayNavHost(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
