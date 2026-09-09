package mg.iray.app.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mg.iray.app.dao.TaskDao
import mg.iray.app.entity.TaskEntity
class TaskRepository(
    private val dao: TaskDao,
    private val firestore: FirebaseFirestore,
    private val userId: String
) {
    private val collection get() = firestore.collection("users/$userId/tasks")

    // --- Lecture : toujours depuis Room ---
    fun observeTasks(): Flow<List<TaskEntity>> = dao.observeTasks()

    // --- Écriture : local d'abord, sync ensuite ---
    suspend fun addOrUpdateTask(task: TaskEntity) {
        dao.upsert(
            task.copy(
                isSynced = false,
                pendingOperation = "UPDATE",
                updatedAt = System.currentTimeMillis()
            )
        )
        // La sync réelle est déléguée à WorkManager (voir plus bas)
    }

    suspend fun deleteTask(id: String) {
        dao.upsert(
            TaskEntity(id = id, title = "", isDeleted = true, pendingOperation = "DELETE")
        )
    }

    // --- Push : envoie les changements locaux vers Firestore ---
    suspend fun pushPendingChanges() {
        val pending = dao.getPendingTasks()
        for (task in pending) {
            try {
                when (task.pendingOperation) {
                    "DELETE" -> {
                        collection.document(task.id).delete().await()
                        dao.hardDelete(task.id)
                    }
                    else -> {
                        collection.document(task.id).set(task.toFirestoreMap()).await()
                        dao.markSynced(task.id)
                    }
                }
            } catch (e: Exception) {
                // Échec réseau : la tâche reste "pending", on réessaiera plus tard
            }
        }
    }

    // --- Pull : récupère les changements distants ---
    suspend fun pullRemoteChanges() {
        val snapshot = collection.get().await()
        val remoteTasks = snapshot.documents.mapNotNull { it.toTaskEntity() }
        // Résolution de conflit simple : "last write wins" via updatedAt
        remoteTasks.forEach { remote ->
            val local = dao.getPendingTasks().find { it.id == remote.id }
            if (local == null || remote.updatedAt > local.updatedAt) {
                dao.upsert(remote.copy(isSynced = true, pendingOperation = null))
            }
        }
    }

    // --- Listener temps réel (optionnel, en plus du pull manuel) ---
    fun startRealtimeSync(scope: CoroutineScope) {
        collection.addSnapshotListener { snapshot, _ ->
            val remoteTasks = snapshot?.documents?.mapNotNull { it.toTaskEntity() } ?: return@addSnapshotListener
            scope.launch {
                dao.upsertAll(remoteTasks.map { it.copy(isSynced = true, pendingOperation = null) })
            }
        }
    }
}

private fun TaskEntity.toFirestoreMap() = mapOf(
    "id" to id, "title" to title, "isDone" to isDone, "updatedAt" to updatedAt
)

private fun DocumentSnapshot.toTaskEntity(): TaskEntity? = try {
    TaskEntity(
        id = getString("id") ?: id,
        title = getString("title") ?: "",
        isDone = getBoolean("isDone") ?: false,
        updatedAt = getLong("updatedAt") ?: 0L
    )
} catch (e: Exception) { null }