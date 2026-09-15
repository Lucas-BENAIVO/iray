package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.ProcedureDao
import mg.iray.app.entity.ProcedureEntity
import mg.iray.app.sync.RemoteSync

class ProcedureRepository(
    private val dao: ProcedureDao,
    private val sync: RemoteSync
) : SyncableRepository {

    private fun collection() = "procedures"

    fun observeActive(): Flow<List<ProcedureEntity>> = dao.observeActive()

    fun observeAll(): Flow<List<ProcedureEntity>> = dao.observeAll()

    fun observeByCategory(category: String): Flow<List<ProcedureEntity>> = dao.observeByCategory(category)

    fun observe(id: String): Flow<ProcedureEntity?> = dao.observe(id)

    override suspend fun pushPendingChanges() {
        // Référentiel administré côté serveur : jamais poussé depuis le client.
    }

    override suspend fun pullRemoteChanges() {
        for (doc in sync.list(collection())) {
            val entity = procedureFromSyncData(doc.id, doc.data) ?: continue
            val local = dao.get(doc.id)
            if (local == null || doc.updatedAt >= local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun procedureFromSyncData(id: String, data: Map<String, Any>): ProcedureEntity? =
    ProcedureEntity(
        id = id,
        name = (data["name"] as? String).orEmpty(),
        description = (data["description"] as? String).orEmpty(),
        category = (data["category"] as? String).orEmpty(),
        requiredDocuments = (data["requiredDocuments"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
        territoryType = (data["territoryType"] as? String).orEmpty(),
        estimatedProcessingDays = (data["estimatedProcessingDays"] as? Number)?.toInt() ?: 0,
        isFree = (data["isFree"] as? Boolean) ?: true,
        isActive = (data["isActive"] as? Boolean) ?: true,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )