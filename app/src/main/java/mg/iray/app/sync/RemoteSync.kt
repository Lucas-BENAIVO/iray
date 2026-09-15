package mg.iray.app.sync

data class RemoteDocument(
    val id: String,
    val data: Map<String, Any>,
    val updatedAt: Long
)

interface RemoteSync {
    suspend fun list(collection: String): List<RemoteDocument>
    suspend fun set(collection: String, documentId: String, data: Map<String, Any>)
    suspend fun delete(collection: String, documentId: String)
}