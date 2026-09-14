package mg.iray.app.sync

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRemoteSync(
    private val firestore: FirebaseFirestore
) : RemoteSync {

    override suspend fun list(collection: String): List<RemoteDocument> {
        return firestore.collection(collection).get().await().documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            RemoteDocument(
                id = doc.id,
                data = data,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }

    override suspend fun set(collection: String, documentId: String, data: Map<String, Any>) {
        firestore.collection(collection).document(documentId).set(data).await()
    }

    override suspend fun delete(collection: String, documentId: String) {
        firestore.collection(collection).document(documentId).delete().await()
    }
}