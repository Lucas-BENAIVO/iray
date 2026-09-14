package mg.iray.app.e2e.gateways

import mg.iray.app.auth.AuthSource
import mg.iray.app.auth.UserSessionStore
import mg.iray.app.entity.MediaEntity
import mg.iray.app.repository.MediaUploader
import mg.iray.app.sync.RemoteDocument
import mg.iray.app.sync.RemoteSync
import java.io.IOException

class InMemorySession : UserSessionStore {
    private var uid: String? = null
    override fun saveUid(uid: String) { this.uid = uid }
    override fun getUid(): String? = uid
    override fun clear() { uid = null }
}

class FakeAuthSource : AuthSource {
    private val queue = ArrayDeque(listOf("anon-e2e-1", "anon-e2e-2"))
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
    override suspend fun linkWithEmail(email: String, password: String): String =
        currentUid ?: error("no current user")
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
    override suspend fun upload(media: MediaEntity): String {
        if (fail) throw IOException("network")
        return "https://firebasestorage.example.com/${media.id}"
    }
}