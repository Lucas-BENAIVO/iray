package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.RequestDao
import mg.iray.app.entity.RequestEntity
import mg.iray.app.sync.RemoteSync

class RequestRepository(
    private val dao: RequestDao,
    private val sync: RemoteSync,
    private val userId: () -> String
) : SyncableRepository {

    private fun collection() = "users/${userId()}/requests"

    fun observeAll(): Flow<List<RequestEntity>> = dao.observeAll(userId())

    fun observeByStatus(status: String): Flow<List<RequestEntity>> = dao.observeByStatus(userId(), status)

    fun observe(id: String): Flow<RequestEntity?> = dao.observe(id)

    suspend fun submit(request: RequestEntity): RequestEntity {
        val now = System.currentTimeMillis()
        val entity = request.copy(
            userId = userId(),
            referenceNumber = request.referenceNumber.ifBlank { generateReference() },
            isSynced = false,
            pendingOperation = "UPDATE",
            updatedAt = now
        )
        dao.upsert(entity)
        return entity
    }

    suspend fun updateStatus(id: String, status: String) {
        val existing = dao.get(id) ?: return
        dao.upsert(
            existing.copy(
                status = status,
                completedAt = if (status == "COMPLETED") System.currentTimeMillis() else existing.completedAt,
                isSynced = false,
                pendingOperation = "UPDATE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(id: String) {
        dao.upsert(
            RequestEntity(
                id = id,
                userId = userId(),
                isDeleted = true,
                pendingOperation = "DELETE",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun pushPendingChanges() {
        for (r in dao.getPending(userId())) {
            try {
                when (r.pendingOperation) {
                    "DELETE" -> {
                        sync.delete(collection(), r.id)
                        dao.hardDelete(r.id)
                    }
                    else -> {
                        sync.set(collection(), r.id, r.toSyncData())
                        dao.markSynced(r.id)
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
            val entity = requestFromSyncData(doc.id, doc.data) ?: continue
            val local = pending.find { it.id == doc.id }
            if (local == null || doc.updatedAt > local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun generateReference(): String {
    val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val seq = (0..999999).random()
    return "REQ-$year-%06d".format(seq)
}

private fun RequestEntity.toSyncData(): Map<String, Any> = buildMap {
    put("id", id)
    put("userId", userId)
    put("procedureId", procedureId)
    put("referenceNumber", referenceNumber)
    put("status", status)
    put("formData", formData)
    put("territoryId", territoryId)
    put("documents", documents)
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
    completedAt?.let { put("completedAt", it) }
    if (isDeleted) put("deleted", true)
}

private fun requestFromSyncData(id: String, data: Map<String, Any>): RequestEntity? =
    RequestEntity(
        id = id,
        userId = (data["userId"] as? String).orEmpty(),
        procedureId = (data["procedureId"] as? String).orEmpty(),
        referenceNumber = (data["referenceNumber"] as? String).orEmpty(),
        status = (data["status"] as? String) ?: "SUBMITTED",
        formData = (data["formData"] as? Map<*, *>)
            ?.entries
            ?.associate { (k, v) -> k.toString() to (v as Any) }
            .orEmpty(),
        territoryId = (data["territoryId"] as? String).orEmpty(),
        documents = (data["documents"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
        createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L,
        completedAt = (data["completedAt"] as? Number)?.toLong()
    )