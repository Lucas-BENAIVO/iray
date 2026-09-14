package mg.iray.app.sync

import mg.iray.app.service.FirestoreApiService
import mg.iray.app.service.FirestoreFieldCodec
import mg.iray.app.service.dto.FirestoreWriteRequest

/**
 * Synchronisation via l'API REST de Firestore (Retrofit), en alternative au SDK.
 * Le token est fourni par FirebaseAuth (currentUser.getIdToken).
 */
class RestRemoteSync(
    private val api: FirestoreApiService,
    private val projectId: String,
    private val tokenProvider: suspend () -> String?
) : RemoteSync {

    override suspend fun list(collection: String): List<RemoteDocument> {
        val response = api.list(projectId, collection, pageSize = null, auth = tokenProvider())
        return response.documents.orEmpty().map { doc ->
            val data = FirestoreFieldCodec.toData(doc.fields)
            RemoteDocument(
                id = doc.name.substringAfterLast('/'),
                data = data,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }

    override suspend fun set(collection: String, documentId: String, data: Map<String, Any>) {
        api.set(
            projectId, collection, documentId,
            FirestoreWriteRequest(FirestoreFieldCodec.toFields(data)),
            data.keys.toList(),
            tokenProvider()
        )
    }

    override suspend fun delete(collection: String, documentId: String) {
        api.delete(projectId, collection, documentId, tokenProvider())
    }
}