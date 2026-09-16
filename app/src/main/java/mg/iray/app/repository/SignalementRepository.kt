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
                referenceNumber = signalement.referenceNumber.ifBlank { generateSignalementReference() },
                status = signalement.status.ifBlank { "RECEIVED" },
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

private fun generateSignalementReference(): String {
    val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val seq = (0..999999).random()
    return "SIG-$year-%06d".format(seq)
}

private fun SignalementEntity.toSyncData(): Map<String, Any> = buildMap {
    put("id", id)
    put("category", category)
    put("subcategory", subcategory)
    put("description", description)
    put("zoneLabel", zoneLabel)
    latitude?.let { put("latitude", it) }
    longitude?.let { put("longitude", it) }
    put("photoMediaIds", photoMediaIds)
    put("referenceNumber", referenceNumber)
    put("status", status)
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
}

private fun signalementFromSyncData(id: String, data: Map<String, Any>): SignalementEntity? =
    SignalementEntity(
        id = id,
        category = (data["category"] as? String).orEmpty(),
        subcategory = (data["subcategory"] as? String).orEmpty(),
        description = (data["description"] as? String).orEmpty(),
        zoneLabel = (data["zoneLabel"] as? String).orEmpty(),
        latitude = (data["latitude"] as? Number)?.toDouble(),
        longitude = (data["longitude"] as? Number)?.toDouble(),
        photoMediaIds = (data["photoMediaIds"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
        referenceNumber = (data["referenceNumber"] as? String).orEmpty(),
        status = (data["status"] as? String) ?: "RECEIVED",
        createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L,
    )
