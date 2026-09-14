package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.TaskDao
import mg.iray.app.entity.TaskEntity
import mg.iray.app.sync.RemoteSync

class TaskRepository(
    private val dao: TaskDao,
    private val sync: RemoteSync,
    private val userId: () -> String
) : SyncableRepository {

    private fun collection() = "users/${userId()}/tasks"

    fun observeTasks(): Flow<List<TaskEntity>> = dao.observeTasks(userId())

    suspend fun addOrUpdateTask(task: TaskEntity) {
        dao.upsert(
            task.copy(
                userId = userId(),
                isSynced = false,
                pendingOperation = "UPDATE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteTask(id: String) {
        dao.upsert(
            TaskEntity(
                id = id,
                userId = userId(),
                title = "",
                isDeleted = true,
                pendingOperation = "DELETE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun pushPendingChanges() {
        for (task in dao.getPendingTasks(userId())) {
            try {
                when (task.pendingOperation) {
                    "DELETE" -> {
                        sync.delete(collection(), task.id)
                        dao.hardDelete(task.id)
                    }
                    else -> {
                        sync.set(collection(), task.id, task.toSyncData())
                        dao.markSynced(task.id)
                    }
                }
            } catch (e: Exception) {
                // hors-ligne : reste "pending", réessayé au prochain cycle
            }
        }
    }

    override suspend fun pullRemoteChanges() {
        val pending = dao.getPendingTasks(userId())
        val remote = sync.list(collection())
        for (doc in remote) {
            val entity = taskFromSyncData(doc.id, doc.data) ?: continue
            val local = pending.find { it.id == doc.id }
            if (local == null || doc.updatedAt > local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun TaskEntity.toSyncData(): Map<String, Any> = mapOf(
    "id" to id,
    "title" to title,
    "isDone" to isDone,
    "updatedAt" to updatedAt
)

private fun taskFromSyncData(id: String, data: Map<String, Any>): TaskEntity? = TaskEntity(
    id = id,
    title = (data["title"] as? String).orEmpty(),
    isDone = (data["isDone"] as? Boolean) ?: false,
    updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
)