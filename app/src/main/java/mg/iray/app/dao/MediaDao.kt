package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.MediaEntity
import mg.iray.app.entity.MediaUploadState

@Dao
interface MediaDao {
    @Query("SELECT * FROM media WHERE ownerType = :ownerType AND ownerId = :ownerId")
    fun observeFor(ownerType: String, ownerId: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeAll(userId: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE uploadState != 'UPLOADED' AND userId = :userId")
    suspend fun getPendingUploads(userId: String): List<MediaEntity>

    @Query("SELECT * FROM media WHERE pendingOperation IS NOT NULL AND userId = :userId")
    suspend fun getPendingSync(userId: String): List<MediaEntity>

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun get(id: String): MediaEntity?

    @Upsert
    suspend fun upsert(media: MediaEntity)

    @Upsert
    suspend fun upsertAll(media: List<MediaEntity>)

    @Query("UPDATE media SET uploadState = :state WHERE id = :id")
    suspend fun setUploadState(id: String, state: MediaUploadState)

    @Query("UPDATE media SET uploadState = 'UPLOADED', remoteUrl = :remoteUrl, isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markUploaded(id: String, remoteUrl: String)

    @Query("UPDATE media SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM media WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("UPDATE media SET userId = :newUid WHERE userId = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}