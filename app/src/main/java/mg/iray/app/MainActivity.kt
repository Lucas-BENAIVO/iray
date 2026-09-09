package mg.iray.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import mg.iray.app.db.AppDatabase
import mg.iray.app.entity.TaskEntity
import mg.iray.app.repository.TaskRepository
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
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.Home -> HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToTasks = { currentScreen = Screen.Tasks }
                        )
                        Screen.Tasks -> TaskScreen(
                            modifier = Modifier.padding(innerPadding),
                            repository = repository,
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onNavigateToTasks: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello iray!")
        Button(onClick = onNavigateToTasks) {
            Text("Aller aux tâches")
        }
    }
}

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    repository: TaskRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val tasks by repository.observeTasks().collectAsState(initial = emptyList())
    var counter by remember { mutableStateOf(1) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.launch {
                val task = TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Tâche test #$counter"
                )
                repository.addOrUpdateTask(task)
                repository.pushPendingChanges() // sync immédiate pour le test
                counter++
            }
        }) {
            Text("Créer une tâche → Firestore")
        }

        Button(onClick = onBack) {
            Text("Retour")
        }

        LazyColumn {
            items(tasks) { task ->
                Text(
                    text = "${task.title} — synced: ${task.isSynced}",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}