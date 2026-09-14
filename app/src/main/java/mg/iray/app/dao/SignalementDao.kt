package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.SignalementEntity

@Dao
interface SignalementDao {
    @Query("SELECT * FROM signalements WHERE isDeleted = 0 AND userId = :userId ORDER BY updatedAt DESC")
    fun observeAll(userId: String): Flow<List<SignalementEntity>>

    @Query("SELECT * FROM signalements WHERE pendingOperation IS NOT NULL AND userId = :userId")
    suspend fun getPending(userId: String): List<SignalementEntity>

    @Query("SELECT * FROM signalements WHERE id = :id")
    suspend fun get(id: String): SignalementEntity?

    @Upsert
    suspend fun upsert(signalement: SignalementEntity)

    @Upsert
    suspend fun upsertAll(signalements: List<SignalementEntity>)

    @Query("UPDATE signalements SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM signalements WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("UPDATE signalements SET userId = :newUid WHERE userId = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}