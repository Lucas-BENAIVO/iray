package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.AnnouncementEntity

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE territoryId = :territoryId OR territoryId IS NULL ORDER BY publishedAt DESC")
    fun observeByTerritory(territoryId: String): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE pendingOperation IS NOT NULL")
    suspend fun getPending(): List<AnnouncementEntity>

    @Query("SELECT * FROM announcements WHERE id = :id")
    suspend fun get(id: String): AnnouncementEntity?

    @Query("SELECT * FROM announcements WHERE id = :id")
    fun observe(id: String): Flow<AnnouncementEntity?>

    @Upsert
    suspend fun upsert(announcement: AnnouncementEntity)

    @Upsert
    suspend fun upsertAll(announcements: List<AnnouncementEntity>)

    @Query("UPDATE announcements SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun hardDelete(id: String)
}
