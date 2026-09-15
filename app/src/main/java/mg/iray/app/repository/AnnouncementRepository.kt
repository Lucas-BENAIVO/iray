package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.AnnouncementDao
import mg.iray.app.entity.AnnouncementEntity
import mg.iray.app.sync.RemoteSync

class AnnouncementRepository(
    private val dao: AnnouncementDao,
    private val sync: RemoteSync
) : SyncableRepository {

    private fun collection() = "announcements"

    fun observeAll(): Flow<List<AnnouncementEntity>> = dao.observeAll()

    fun observeByTerritory(territoryId: String): Flow<List<AnnouncementEntity>> = dao.observeByTerritory(territoryId)

    fun observe(id: String): Flow<AnnouncementEntity?> = dao.observe(id)

    override suspend fun pushPendingChanges() {
        // Annonces publiées par l'administration : jamais poussées depuis le client.
    }

    override suspend fun pullRemoteChanges() {
        for (doc in sync.list(collection())) {
            val entity = announcementFromSyncData(doc.id, doc.data) ?: continue
            val local = dao.get(doc.id)
            if (local == null || doc.updatedAt >= local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun announcementFromSyncData(id: String, data: Map<String, Any>): AnnouncementEntity? =
    AnnouncementEntity(
        id = id,
        title = (data["title"] as? String).orEmpty(),
        content = (data["content"] as? String).orEmpty(),
        territoryId = data["territoryId"] as? String,
        publishedAt = (data["publishedAt"] as? Number)?.toLong() ?: 0L,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )