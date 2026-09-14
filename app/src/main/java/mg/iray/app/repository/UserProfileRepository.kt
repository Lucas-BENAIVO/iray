package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.UserProfileDao
import mg.iray.app.entity.UserProfileEntity
import mg.iray.app.sync.RemoteSync

class UserProfileRepository(
    private val dao: UserProfileDao,
    private val sync: RemoteSync,
    private val userId: () -> String
) : SyncableRepository {

    private fun collection() = "users/${userId()}/profile"

    fun observeProfile(): Flow<UserProfileEntity?> = dao.observeProfile(userId())

    suspend fun saveProfile(
        firstName: String = "",
        lastName: String = "",
        phone: String = "",
        email: String = "",
        avatarLocalUri: String? = null
    ) {
        val uid = userId()
        val profile = (dao.get(uid) ?: UserProfileEntity(uid = uid)).copy(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            avatarLocalUri = avatarLocalUri ?: dao.get(uid)?.avatarLocalUri,
            isSynced = false,
            pendingOperation = "UPDATE",
            updatedAt = System.currentTimeMillis()
        )
        dao.upsert(profile)
    }

    override suspend fun pushPendingChanges() {
        for (profile in dao.getPending()) {
            try {
                sync.set(collection(), profile.uid, profile.toSyncData())
                dao.markSynced(profile.uid)
            } catch (e: Exception) {
                // hors-ligne : reste pending
            }
        }
    }

    override suspend fun pullRemoteChanges() {
        val pending = dao.getPending()
        for (doc in sync.list(collection())) {
            val profile = profileFromSyncData(doc.id, doc.data) ?: continue
            val local = pending.find { it.uid == doc.id }
            if (local == null || doc.updatedAt > local.updatedAt) {
                dao.upsert(profile.copy(isSynced = true, pendingOperation = null))
            }
        }
    }
}

private fun UserProfileEntity.toSyncData(): Map<String, Any> = mapOf(
    "uid" to uid,
    "firstName" to firstName,
    "lastName" to lastName,
    "phone" to phone,
    "email" to email,
    "avatarRemoteUrl" to (avatarRemoteUrl ?: ""),
    "updatedAt" to updatedAt
)

private fun profileFromSyncData(id: String, data: Map<String, Any>): UserProfileEntity? =
    UserProfileEntity(
        uid = id,
        firstName = (data["firstName"] as? String).orEmpty(),
        lastName = (data["lastName"] as? String).orEmpty(),
        phone = (data["phone"] as? String).orEmpty(),
        email = (data["email"] as? String).orEmpty(),
        avatarRemoteUrl = (data["avatarRemoteUrl"] as? String)?.takeIf { it.isNotBlank() },
        updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
    )