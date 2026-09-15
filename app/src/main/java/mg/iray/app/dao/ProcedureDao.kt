package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.ProcedureEntity

@Dao
interface ProcedureDao {
    @Query("SELECT * FROM procedures WHERE isActive = 1 ORDER BY name ASC")
    fun observeActive(): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM procedures ORDER BY name ASC")
    fun observeAll(): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM procedures WHERE category = :category AND isActive = 1 ORDER BY name ASC")
    fun observeByCategory(category: String): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM procedures WHERE pendingOperation IS NOT NULL")
    suspend fun getPending(): List<ProcedureEntity>

    @Query("SELECT * FROM procedures WHERE id = :id")
    suspend fun get(id: String): ProcedureEntity?

    @Query("SELECT * FROM procedures WHERE id = :id")
    fun observe(id: String): Flow<ProcedureEntity?>

    @Upsert
    suspend fun upsert(procedure: ProcedureEntity)

    @Upsert
    suspend fun upsertAll(procedures: List<ProcedureEntity>)

    @Query("UPDATE procedures SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM procedures WHERE id = :id")
    suspend fun hardDelete(id: String)
}
