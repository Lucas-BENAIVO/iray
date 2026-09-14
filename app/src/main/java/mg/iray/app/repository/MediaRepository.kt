package mg.iray.app.repository

import kotlinx.coroutines.flow.Flow
import mg.iray.app.dao.MediaDao
import mg.iray.app.entity.MediaEntity
import mg.iray.app.entity.MediaUploadState
import java.util.UUID

interface MediaUploader {
    /** Téléverse le fichier local et retourne son URL Firebase Storage. */
    suspend fun upload(media: MediaEntity): String
}

class MediaRepository(
    private val dao: MediaDao,
    private val uploader: MediaUploader,
    private val userId: () -> String
) {

    fun observeFor(ownerType: String, ownerId: String): Flow<List<MediaEntity>> =
        dao.observeFor(ownerType, ownerId)

    suspend fun saveLocally(
        uri: String,
        mimeType: String,
        ownerType: String = "GENERIC",
        ownerId: String? = null
    ): MediaEntity {
        val media = MediaEntity(
            id = UUID.randomUUID().toString(),
            userId = userId(),
            localUri = uri,
            mimeType = mimeType,
            ownerType = ownerType,
            ownerId = ownerId,
            uploadState = MediaUploadState.PENDING,
            isSynced = false,
            pendingOperation = "CREATE"
        )
        dao.upsert(media)
        return media
    }

    /** Téléverse tous les médias non encore envoyés (PENDING ou en échec). */
    suspend fun uploadPending(): Int {
        var uploaded = 0
        for (media in dao.getPendingUploads(userId())) {
            try {
                val remoteUrl = uploader.upload(media)
                dao.markUploaded(media.id, remoteUrl)
                uploaded++
            } catch (e: Exception) {
                dao.setUploadState(media.id, MediaUploadState.FAILED)
            }
        }
        return uploaded
    }

    suspend fun deleteLocal(id: String) {
        dao.hardDelete(id)
    }
}