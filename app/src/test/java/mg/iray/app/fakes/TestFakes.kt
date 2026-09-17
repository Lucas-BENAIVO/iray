package mg.iray.app.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import mg.iray.app.auth.AuthSource
import mg.iray.app.auth.UserSessionStore
import mg.iray.app.dao.MediaDao
import mg.iray.app.dao.NotificationDao
import mg.iray.app.dao.RequestDao
import mg.iray.app.dao.SignalementDao
import mg.iray.app.dao.TaskDao
import mg.iray.app.dao.UserDao
import mg.iray.app.dao.UserProfileDao
import mg.iray.app.entity.MediaEntity
import mg.iray.app.entity.MediaUploadState
import mg.iray.app.entity.NotificationEntity
import mg.iray.app.entity.RequestEntity
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.entity.TaskEntity
import mg.iray.app.entity.UserEntity
import mg.iray.app.entity.UserProfileEntity
import mg.iray.app.repository.MediaUploader
import mg.iray.app.sync.RemoteDocument
import mg.iray.app.sync.RemoteSync
import java.io.IOException

class InMemorySession : UserSessionStore {
    private val uidFlow = MutableStateFlow<String?>(null)
    override fun saveUid(uid: String) { uidFlow.value = uid }
    override fun getUid(): String? = uidFlow.value
    override fun observeUid() = uidFlow
    override fun clear() { uidFlow.value = null }
}

class FakeAuthSource : AuthSource {
    private val queue = ArrayDeque(listOf("anon-1", "anon-2", "anon-3"))
    var fail = false
    var currentUid: String? = null
    override fun currentUserId(): String? = currentUid
    override fun isAnonymous(): Boolean = true
    override suspend fun signInAnonymously(): String {
        if (fail) throw IOException("offline")
        val uid = queue.removeFirstOrNull() ?: "anon-${System.nanoTime()}"
        currentUid = uid
        return uid
    }
    override suspend fun linkWithEmail(email: String, password: String): String {
        if (fail) throw IOException("offline")
        return currentUid ?: error("no current user")
    }
}

class FakeRemoteSync : RemoteSync {
    val docs = mutableMapOf<String, MutableMap<String, Pair<Map<String, Any>, Long>>>()
    var failList = false
    var failSet = false
    var failDelete = false

    override suspend fun list(collection: String): List<RemoteDocument> {
        if (failList) throw IOException("offline")
        return docs[collection].orEmpty().map { (id, v) -> RemoteDocument(id, v.first, v.second) }
    }

    override suspend fun set(collection: String, documentId: String, data: Map<String, Any>) {
        if (failSet) throw IOException("offline")
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
        docs.getOrPut(collection) { mutableMapOf() }[documentId] = data to updatedAt
    }

    override suspend fun delete(collection: String, documentId: String) {
        if (failDelete) throw IOException("offline")
        docs[collection]?.remove(documentId)
    }

    fun contains(collection: String, documentId: String): Boolean =
        docs[collection]?.containsKey(documentId) == true
}

class FakeMediaUploader : MediaUploader {
    var fail = false
    val uploadedIds = mutableListOf<String>()
    override suspend fun upload(media: MediaEntity): String {
        if (fail) throw IOException("network")
        uploadedIds.add(media.id)
        return "https://firebasestorage.example.com/${media.id}"
    }
}

class FakeUserDao : UserDao {
    var store = mutableMapOf<String, UserEntity>()
    private val flow = MutableStateFlow<UserEntity?>(null)

    override fun observeUser(uid: String): Flow<UserEntity?> = flow
    override suspend fun getFirst(): UserEntity? = store.values.firstOrNull()
    override suspend fun get(uid: String): UserEntity? = store[uid]
    override suspend fun upsert(user: UserEntity) { store[user.uid] = user; flow.value = user }
    override suspend fun delete(uid: String) { store.remove(uid) }
    override suspend fun markSynced(uid: String) {
        store[uid]?.let { store[uid] = it.copy(isSynced = true, pendingOperation = null) }
    }
    override suspend fun rewriteUid(oldUid: String, newUid: String) {
        store.remove(oldUid)?.copy(uid = newUid)?.let { store[newUid] = it }
    }

    fun all(): List<UserEntity> = store.values.toList()
}

class FakeUserProfileDao : UserProfileDao {
    var store = mutableMapOf<String, UserProfileEntity>()
    private val flow = MutableStateFlow<UserProfileEntity?>(null)

    override fun observeProfile(uid: String): Flow<UserProfileEntity?> = flow
    override suspend fun get(uid: String): UserProfileEntity? = store[uid]
    override suspend fun getAll(): List<UserProfileEntity> = store.values.toList()
    override suspend fun getPending(): List<UserProfileEntity> = store.values.filter { it.pendingOperation != null }
    override suspend fun upsert(profile: UserProfileEntity) { store[profile.uid] = profile; flow.value = profile }
    override suspend fun upsertAll(profiles: List<UserProfileEntity>) { profiles.forEach { upsert(it) } }
    override suspend fun markSynced(uid: String) {
        store[uid]?.let { store[uid] = it.copy(isSynced = true, pendingOperation = null) }
    }
    override suspend fun rewriteUid(oldUid: String, newUid: String) {
        store.remove(oldUid)?.copy(uid = newUid)?.let { store[newUid] = it }
    }
}

class FakeTaskDao : TaskDao {
    var store = mutableListOf<TaskEntity>()
    private val flow = MutableStateFlow<List<TaskEntity>>(emptyList())

    override fun observeTasks(userId: String): Flow<List<TaskEntity>> = flow
    override suspend fun getPendingTasks(userId: String): List<TaskEntity> =
        store.filter { it.pendingOperation != null && it.userId == userId }
    override suspend fun get(id: String): TaskEntity? = store.find { it.id == id }
    override suspend fun upsert(task: TaskEntity) { store.removeAll { it.id == task.id }; store.add(task); refresh() }
    override suspend fun upsertAll(tasks: List<TaskEntity>) { tasks.forEach { upsert(it) } }
    override suspend fun markSynced(id: String) {
        store.replaceAll { if (it.id == id) it.copy(isSynced = true, pendingOperation = null) else it }; refresh()
    }
    override suspend fun hardDelete(id: String) { store.removeAll { it.id == id }; refresh() }
    override suspend fun rewriteUid(oldUid: String, newUid: String) {
        store.replaceAll { if (it.userId == oldUid) it.copy(userId = newUid) else it }; refresh()
    }

    private fun refresh() { flow.value = store.toList() }
    fun all(): List<TaskEntity> = store.toList()
}

class FakeMediaDao : MediaDao {
    var store = mutableMapOf<String, MediaEntity>()
    private val flow = MutableStateFlow<List<MediaEntity>>(emptyList())

    override fun observeFor(ownerType: String, ownerId: String): Flow<List<MediaEntity>> = flow
    override fun observeAll(userId: String): Flow<List<MediaEntity>> = flow
    override suspend fun getPendingUploads(userId: String): List<MediaEntity> =
        store.values.filter { it.userId == userId && it.uploadState != MediaUploadState.UPLOADED }
    override suspend fun getPendingSync(userId: String): List<MediaEntity> =
        store.values.filter { it.userId == userId && it.pendingOperation != null }
    override suspend fun get(id: String): MediaEntity? = store[id]
    override suspend fun upsert(media: MediaEntity) { store[media.id] = media; refresh() }
    override suspend fun upsertAll(media: List<MediaEntity>) { media.forEach { upsert(it) }; refresh() }
    override suspend fun setUploadState(id: String, state: MediaUploadState) {
        store[id]?.let { store[id] = it.copy(uploadState = state) }; refresh()
    }
    override suspend fun markUploaded(id: String, remoteUrl: String) {
        store[id]?.let { store[id] = it.copy(uploadState = MediaUploadState.UPLOADED, remoteUrl = remoteUrl, isSynced = true, pendingOperation = null) }; refresh()
    }
    override suspend fun markSynced(id: String) {
        store[id]?.let { store[id] = it.copy(isSynced = true, pendingOperation = null) }; refresh()
    }
    override suspend fun hardDelete(id: String) { store.remove(id); refresh() }
    override suspend fun rewriteUid(oldUid: String, newUid: String) {
        store = store.mapValues { if (it.value.userId == oldUid) it.value.copy(userId = newUid) else it.value }.toMutableMap(); refresh()
    }

    private fun refresh() { flow.value = store.values.toList() }
    fun all(): List<MediaEntity> = store.values.toList()
}

class FakeSignalementDao : SignalementDao {
    var store = mutableListOf<SignalementEntity>()
    private val flow = MutableStateFlow<List<SignalementEntity>>(emptyList())

    override fun observeAll(userId: String): Flow<List<SignalementEntity>> = flow
    override suspend fun getPending(userId: String): List<SignalementEntity> =
        store.filter { it.pendingOperation != null && it.userId == userId }
    override suspend fun get(id: String): SignalementEntity? = store.find { it.id == id }
    override suspend fun upsert(signalement: SignalementEntity) { store.removeAll { it.id == signalement.id }; store.add(signalement); refresh() }
    override suspend fun upsertAll(signalements: List<SignalementEntity>) { signalements.forEach { upsert(it) } }
    override suspend fun markSynced(id: String) { store.replaceAll { if (it.id == id) it.copy(isSynced = true, pendingOperation = null) else it }; refresh() }
    override suspend fun hardDelete(id: String) { store.removeAll { it.id == id }; refresh() }
    override suspend fun rewriteUid(oldUid: String, newUid: String) { store.replaceAll { if (it.userId == oldUid) it.copy(userId = newUid) else it }; refresh() }

    private fun refresh() { flow.value = store.toList() }
    fun all(): List<SignalementEntity> = store.toList()
}

class FakeNotificationDao : NotificationDao {
    var store = mutableListOf<NotificationEntity>()
    private val flow = MutableStateFlow<List<NotificationEntity>>(emptyList())

    override fun observeAll(userId: String): Flow<List<NotificationEntity>> = flow
    override suspend fun getPending(userId: String): List<NotificationEntity> =
        store.filter { it.pendingOperation != null && it.userId == userId }
    override suspend fun get(id: String): NotificationEntity? = store.find { it.id == id }
    override suspend fun upsert(notification: NotificationEntity) { store.removeAll { it.id == notification.id }; store.add(notification); refresh() }
    override suspend fun upsertAll(notifications: List<NotificationEntity>) { notifications.forEach { upsert(it) } }
    override suspend fun markRead(id: String) { store.replaceAll { if (it.id == id) it.copy(isRead = true) else it }; refresh() }
    override suspend fun markSynced(id: String) { store.replaceAll { if (it.id == id) it.copy(isSynced = true, pendingOperation = null) else it }; refresh() }
    override suspend fun hardDelete(id: String) { store.removeAll { it.id == id }; refresh() }
    override suspend fun rewriteUid(oldUid: String, newUid: String) { store.replaceAll { if (it.userId == oldUid) it.copy(userId = newUid) else it }; refresh() }

    private fun refresh() { flow.value = store.toList() }
    fun all(): List<NotificationEntity> = store.toList()
}

class FakeRequestDao : RequestDao {
    var store = mutableListOf<RequestEntity>()
    private val flow = MutableStateFlow<List<RequestEntity>>(emptyList())
    private val flowItem = MutableStateFlow<RequestEntity?>(null)

    override fun observeAll(userId: String): Flow<List<RequestEntity>> = flow
    override fun observeByStatus(userId: String, status: String): Flow<List<RequestEntity>> = flow
    override fun observe(id: String): Flow<RequestEntity?> = flowItem
    override suspend fun getPending(userId: String): List<RequestEntity> =
        store.filter { it.pendingOperation != null && it.userId == userId }
    override suspend fun get(id: String): RequestEntity? = store.find { it.id == id }
    override suspend fun upsert(request: RequestEntity) { store.removeAll { it.id == request.id }; store.add(request); refresh() }
    override suspend fun upsertAll(requests: List<RequestEntity>) { requests.forEach { upsert(it) } }
    override suspend fun markSynced(id: String) { store.replaceAll { if (it.id == id) it.copy(isSynced = true, pendingOperation = null) else it }; refresh() }
    override suspend fun hardDelete(id: String) { store.removeAll { it.id == id }; refresh() }
    override suspend fun rewriteUid(oldUid: String, newUid: String) { store.replaceAll { if (it.userId == oldUid) it.copy(userId = newUid) else it }; refresh() }

    private fun refresh() { flow.value = store.toList() }
    fun all(): List<RequestEntity> = store.toList()
}