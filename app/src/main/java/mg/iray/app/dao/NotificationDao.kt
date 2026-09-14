package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.NotificationEntity

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY sentAt DESC")
    fun observeAll(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE pendingOperation IS NOT NULL AND userId = :userId")
    suspend fun getPending(userId: String): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun get(id: String): NotificationEntity?

    @Upsert
    suspend fun upsert(notification: NotificationEntity)

    @Upsert
    suspend fun upsertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notifications SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("UPDATE notifications SET userId = :newUid WHERE userId = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}