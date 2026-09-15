package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.RequestEntity

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests WHERE isDeleted = 0 AND userId = :userId ORDER BY updatedAt DESC")
    fun observeAll(userId: String): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE userId = :userId AND status = :status AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun observeByStatus(userId: String, status: String): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE pendingOperation IS NOT NULL AND userId = :userId")
    suspend fun getPending(userId: String): List<RequestEntity>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun get(id: String): RequestEntity?

    @Query("SELECT * FROM requests WHERE id = :id")
    fun observe(id: String): Flow<RequestEntity?>

    @Upsert
    suspend fun upsert(request: RequestEntity)

    @Upsert
    suspend fun upsertAll(requests: List<RequestEntity>)

    @Query("UPDATE requests SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM requests WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("UPDATE requests SET userId = :newUid WHERE userId = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}
