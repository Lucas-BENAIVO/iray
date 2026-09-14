package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mg.iray.app.auth.AuthSource
import mg.iray.app.auth.UserSessionStore
import mg.iray.app.dao.MediaDao
import mg.iray.app.dao.NotificationDao
import mg.iray.app.dao.SignalementDao
import mg.iray.app.dao.TaskDao
import mg.iray.app.dao.UserDao
import mg.iray.app.dao.UserProfileDao
import mg.iray.app.entity.UserEntity
import mg.iray.app.entity.UserProfileEntity
import java.util.UUID

/**
 * Flux utilisateur offline-first (cf auth.md) :
 * 1. Premier lancement -> signInAnonymously() -> uid stable, persisté localement.
 * 2. Entrées futures    -> uid relu depuis le stockage local (même hors-ligne).
 * 3. Ajout d'infos      -> écrit localement (Room) puis synchronisé.
 * 4. Identification     -> linkWithCredential() -> le uid ne change pas.
 */
class AuthRepository(
    private val auth: AuthSource,
    private val session: UserSessionStore,
    private val userDao: UserDao,
    private val profileDao: UserProfileDao,
    private val taskDao: TaskDao,
    private val signalementDao: SignalementDao,
    private val mediaDao: MediaDao,
    private val notificationDao: NotificationDao
) {

    suspend fun ensureUserId(): String {
        session.getUid()?.let { return it }
        val uid = runCatching { auth.signInAnonymously() }
            .getOrElse { "local-${UUID.randomUUID()}" }
        session.saveUid(uid)
        userDao.upsert(UserEntity(uid = uid, isAnonymous = uid.startsWith("local-"), updatedAt = now()))
        return uid
    }

    suspend fun currentUid(): String? = session.getUid()

    fun observeCurrentUser(): Flow<UserEntity?> {
        val uid = session.getUid() ?: return flow { emit(null) }
        return userDao.observeUser(uid)
    }

    suspend fun addProfileInfo(
        firstName: String = "",
        lastName: String = "",
        phone: String = "",
        email: String = ""
    ): UserProfileEntity {
        val uid = ensureUserId()
        val existing = profileDao.get(uid)
        val profile = (existing ?: UserProfileEntity(uid = uid)).copy(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            isSynced = false,
            pendingOperation = "UPDATE",
            updatedAt = now()
        )
        profileDao.upsert(profile)
        return profile
    }

    suspend fun linkWithEmail(email: String, password: String): UserEntity {
        val uid = auth.linkWithEmail(email, password)
        val user = UserEntity(
            uid = uid,
            isAnonymous = false,
            email = email,
            isSynced = true,
            pendingOperation = "UPDATE",
            updatedAt = now()
        )
        session.saveUid(uid)
        userDao.upsert(user)
        return user
    }

    /** Après un premier lancement hors-ligne (uid "local-..."), remplace par le vrai uid Firebase dès que le réseau revient. */
    suspend fun ensureRealUid(): String {
        val uid = session.getUid() ?: return ensureUserId()
        if (!uid.startsWith("local-")) return uid
        val real = runCatching { auth.signInAnonymously() }.getOrElse { return uid }
        if (real == uid) return real
        rewriteLocalUid(uid, real)
        return real
    }

    suspend fun rewriteLocalUid(oldUid: String, newUid: String) {
        if (oldUid == newUid) return
        session.saveUid(newUid)
        userDao.rewriteUid(oldUid, newUid)
        profileDao.rewriteUid(oldUid, newUid)
        taskDao.rewriteUid(oldUid, newUid)
        signalementDao.rewriteUid(oldUid, newUid)
        mediaDao.rewriteUid(oldUid, newUid)
        notificationDao.rewriteUid(oldUid, newUid)
    }

    private fun now() = System.currentTimeMillis()
}