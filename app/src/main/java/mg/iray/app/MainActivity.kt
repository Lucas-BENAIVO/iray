package mg.iray.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import mg.iray.app.ui.navigation.IrayNavHost
import mg.iray.app.ui.theme.IrayTheme
import java.util.UUID

sealed class Screen {
    object Home : Screen()
    object Tasks : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = TaskRepository(
            dao = db.taskDao(),
            firestore = FirebaseFirestore.getInstance(),
            userId = "test-user" // provisoire, sans Auth
        )

        setContent {
            IrayTheme {
                IrayNavHost(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
