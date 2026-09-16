package mg.iray.app.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.TerritoryDao
import mg.iray.app.entity.TerritoryEntity
import mg.iray.app.sync.RemoteSync
import org.json.JSONArray
import org.json.JSONObject

class TerritoryRepository(
    private val dao: TerritoryDao,
    private val sync: RemoteSync,
    private val appContext: Context? = null,
) : SyncableRepository {

    private fun collection() = "territories"

    fun observeAll(): Flow<List<TerritoryEntity>> = dao.observeAll()

    fun observeByParent(parentId: String): Flow<List<TerritoryEntity>> = dao.observeByParent(parentId)

    fun observeByType(type: String): Flow<List<TerritoryEntity>> = dao.observeByType(type)

    fun observeRoots(): Flow<List<TerritoryEntity>> = dao.observeRoots()

    fun observe(id: String): Flow<TerritoryEntity?> = dao.observe(id)

    suspend fun count(): Int = dao.count()

    override suspend fun pushPendingChanges() {
        // Référentiel administré côté serveur : jamais poussé depuis le client.
    }

    override suspend fun pullRemoteChanges() {
        // Offline-first : baseline locale d’abord, puis enrichissement réseau.
        ensureLocalReferentiel()
        val docs = try {
            sync.list(collection())
        } catch (e: Exception) {
            Log.w(TAG, "pull territories offline/unavailable: ${e.message}")
            return
        }
        Log.d(TAG, "pull territories remote=${docs.size}")
        var upserted = 0
        for (doc in docs) {
            val entity = territoryFromSyncData(doc.id, doc.data) ?: continue
            val local = dao.get(doc.id)
            if (local == null || doc.updatedAt >= local.updatedAt) {
                dao.upsert(entity.copy(isSynced = true, pendingOperation = null))
                upserted++
            }
        }
        Log.d(
            TAG,
            "pull territories upserted=$upserted " +
                "local=${dao.count()} " +
                "R=${dao.countByType("REGION")} D=${dao.countByType("DISTRICT")} " +
                "C=${dao.countByType("COMMUNE")} F=${dao.countByType("FOKONTANY")}",
        )
    }

    /**
     * Référentiel offline-first : upsert le seed assets
     * (région → … → fokontany) pour garantir la cascade hors-ligne.
     */
    suspend fun ensureLocalReferentiel() {
        val entities = loadSeedEntities() ?: return
        dao.upsertAll(entities)
        Log.d(
            TAG,
            "local referentiel ready size=${entities.size} " +
                "R=${dao.countByType("REGION")} D=${dao.countByType("DISTRICT")} " +
                "C=${dao.countByType("COMMUNE")} A=${dao.countByType("ARRONDISSEMENT")} " +
                "F=${dao.countByType("FOKONTANY")}",
        )
    }

    /** @deprecated use [ensureLocalReferentiel] */
    suspend fun ensureSeedIfEmpty() = ensureLocalReferentiel()

    private fun loadSeedEntities(): List<TerritoryEntity>? {
        val ctx = appContext ?: return null
        return runCatching {
            val json = ctx.assets.open(SEED_ASSET).bufferedReader().use { it.readText() }
            val arr = JSONArray(json)
            val now = System.currentTimeMillis()
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        TerritoryEntity(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            type = o.getString("type").uppercase(),
                            parentId = o.parentIdOrNull(),
                            updatedAt = now,
                            isSynced = true,
                            pendingOperation = null,
                        ),
                    )
                }
            }
        }.onFailure {
            Log.e(TAG, "load seed territories failed", it)
        }.getOrNull()
    }

    companion object {
        private const val TAG = "IrayTerritory"
        private const val SEED_ASSET = "territories_seed.json"
    }
}

private fun JSONObject.parentIdOrNull(): String? {
    if (!has("parentId") || isNull("parentId")) return null
    return optString("parentId", "").trim().ifBlank { null }
}

internal fun territoryFromSyncData(id: String, data: Map<String, Any>): TerritoryEntity? {
    val name = firstString(data, "name", "label", "nom", "title") ?: return null
    val type = firstString(data, "type", "level", "territoryType")?.uppercase().orEmpty()
    val parentId = parentIdFrom(data["parentId"] ?: data["parent_id"] ?: data["parent"])
    val updatedAt = (data["updatedAt"] as? Number)?.toLong()
        ?: (data["updated_at"] as? Number)?.toLong()
        ?: 0L
    return TerritoryEntity(
        id = id,
        name = name,
        type = type,
        parentId = parentId,
        updatedAt = updatedAt,
    )
}

private fun firstString(data: Map<String, Any>, vararg keys: String): String? {
    for (key in keys) {
        val v = data[key] as? String
        if (!v.isNullOrBlank()) return v.trim()
    }
    return null
}

private fun parentIdFrom(raw: Any?): String? = when (raw) {
    null -> null
    is String -> raw.trim().ifBlank { null }
    is DocumentReference -> raw.id
    else -> raw.toString().substringAfterLast('/').ifBlank { null }
}
