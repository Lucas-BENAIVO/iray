package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.TerritoryDao
import mg.iray.app.entity.TerritoryEntity
import mg.iray.app.sync.RemoteSync

class TerritoryRepository(
    private val dao: TerritoryDao,
    private val sync: RemoteSync
) : SyncableRepository {

    private fun collection() = "territories"

    fun observeAll(): Flow<List<TerritoryEntity>> = dao.observeAll()

    fun observeByParent(parentId: String): Flow<List<TerritoryEntity>> = dao.observeByParent(parentId)

    fun observeByType(type: String): Flow<List<TerritoryEntity>> = dao.observeByType(type)

    fun observe(id: String): Flow<TerritoryEntity?> = dao.observe(id)

    override suspend fun pushPendingChanges() {
        // Référentiel administré côté serveur : jamais poussé depuis le client.
    }

    override suspend fun pullRemoteChanges() {
        for (doc in sync.list(collection())) {
            val entity = territoryFromSyncData(doc.id, doc.data) ?: continue
            val local = dao.get(doc.id)
            if (local == null || doc.updatedAt >= local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun territoryFromSyncData(id: String, data: Map<String, Any>): TerritoryEntity? =
    TerritoryEntity(
        id = id,
        name = (data["name"] as? String).orEmpty(),
        type = (data["type"] as? String).orEmpty(),
        parentId = data["parentId"] as? String,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )