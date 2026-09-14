package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.NotificationDao
import mg.iray.app.entity.NotificationEntity
import mg.iray.app.sync.RemoteSync

class NotificationRepository(
    private val dao: NotificationDao,
    private val sync: RemoteSync,
    private val userId: () -> String
) : SyncableRepository {

    private fun collection() = "users/${userId()}/notifications"

    fun observeAll(): Flow<List<NotificationEntity>> = dao.observeAll(userId())

    suspend fun save(notification: NotificationEntity) {
        dao.upsert(
            notification.copy(
                userId = userId(),
                isSynced = false,
                pendingOperation = "UPDATE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun markRead(id: String) {
        dao.markRead(id)
    }

    override suspend fun pushPendingChanges() {
        for (n in dao.getPending(userId())) {
            try {
                if (n.pendingOperation == "DELETE") {
                    sync.delete(collection(), n.id)
                    dao.hardDelete(n.id)
                } else {
                    sync.set(collection(), n.id, n.toSyncData())
                    dao.markSynced(n.id)
                }
            } catch (e: Exception) {
                // hors-ligne : reste pending
            }
        }
    }

    override suspend fun pullRemoteChanges() {
        val pending = dao.getPending(userId())
        for (doc in sync.list(collection())) {
            val entity = notificationFromSyncData(doc.id, doc.data) ?: continue
            val local = pending.find { it.id == doc.id }
            if (local == null || doc.updatedAt > local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun NotificationEntity.toSyncData(): Map<String, Any> = mapOf(
    "id" to id,
    "title" to title,
    "body" to body,
    "isRead" to isRead,
    "sentAt" to sentAt,
    "updatedAt" to updatedAt
)

private fun notificationFromSyncData(id: String, data: Map<String, Any>): NotificationEntity? =
    NotificationEntity(
        id = id,
        title = (data["title"] as? String).orEmpty(),
        body = (data["body"] as? String).orEmpty(),
        isRead = (data["isRead"] as? Boolean) ?: false,
        sentAt = (data["sentAt"] as? Number)?.toLong() ?: 0L,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )