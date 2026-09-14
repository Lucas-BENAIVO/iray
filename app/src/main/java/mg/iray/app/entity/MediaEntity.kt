package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaUploadState { PENDING, UPLOADED, FAILED }

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,               // UUID généré côté client
    val userId: String = "",                  // uid Firebase propriétaire
    val localUri: String,                     // uri du fichier local
    val mimeType: String = "application/octet-stream",
    val sizeBytes: Long = 0L,
    val ownerType: String = "GENERIC",        // TASK | PROFILE | SIGNALEMENT | ...
    val ownerId: String? = null,              // id de l'entité propriétaire
    val remoteUrl: String? = null,            // url Firebase Storage après upload
    val uploadState: MediaUploadState = MediaUploadState.PENDING,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val pendingOperation: String? = null
)