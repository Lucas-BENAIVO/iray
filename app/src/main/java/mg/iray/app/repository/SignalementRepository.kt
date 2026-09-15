package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.SignalementDao
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.sync.RemoteSync

class SignalementRepository(
    private val dao: SignalementDao,
    private val sync: RemoteSync,
    private val userId: () -> String
) : SyncableRepository {

    private fun collection() = "users/${userId()}/signalements"

    fun observeAll(): Flow<List<SignalementEntity>> = dao.observeAll(userId())

    suspend fun save(signalement: SignalementEntity) {
        dao.upsert(
            signalement.copy(
                userId = userId(),
                isSynced = false,
                pendingOperation = "UPDATE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(id: String) {
        dao.upsert(
            SignalementEntity(
                id = id,
                userId = userId(),
                isDeleted = true,
                pendingOperation = "DELETE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun pushPendingChanges() {
        for (s in dao.getPending(userId())) {
            try {
                when (s.pendingOperation) {
                    "DELETE" -> {
                        sync.delete(collection(), s.id)
                        dao.hardDelete(s.id)
                    }
                    else -> {
                        sync.set(collection(), s.id, s.toSyncData())
                        dao.markSynced(s.id)
                    }
                }
            } catch (e: Exception) {
                // hors-ligne : reste pending
            }
        }
    }

    override suspend fun pullRemoteChanges() {
        val pending = dao.getPending(userId())
        for (doc in sync.list(collection())) {
            val entity = signalementFromSyncData(doc.id, doc.data) ?: continue
            val local = pending.find { it.id == doc.id }
            if (local == null || doc.updatedAt > local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun SignalementEntity.toSyncData(): Map<String, Any> = mapOf(
    "id" to id,
    "category" to category,
    "subcategory" to subcategory,
    "description" to description,
    "zoneLabel" to zoneLabel,
    "latitude" to (latitude ?: 0.0),
    "longitude" to (longitude ?: 0.0),
    "photoMediaIds" to photoMediaIds,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun signalementFromSyncData(id: String, data: Map<String, Any>): SignalementEntity? =
    SignalementEntity(
        id = id,
        category = (data["category"] as? String).orEmpty(),
        subcategory = (data["subcategory"] as? String).orEmpty(),
        description = (data["description"] as? String).orEmpty(),
        zoneLabel = (data["zoneLabel"] as? String).orEmpty(),
        latitude = (data["latitude"] as? Double),
        longitude = (data["longitude"] as? Double),
        photoMediaIds = (data["photoMediaIds"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
        createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )