package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.TerritoryEntity

@Dao
interface TerritoryDao {
    @Query("SELECT * FROM territories ORDER BY name ASC")
    fun observeAll(): Flow<List<TerritoryEntity>>

    @Query("SELECT * FROM territories WHERE parentId = :parentId ORDER BY name ASC")
    fun observeByParent(parentId: String): Flow<List<TerritoryEntity>>

    @Query("SELECT * FROM territories WHERE type = :type ORDER BY name ASC")
    fun observeByType(type: String): Flow<List<TerritoryEntity>>

    @Query("SELECT * FROM territories WHERE pendingOperation IS NOT NULL")
    suspend fun getPending(): List<TerritoryEntity>

    @Query("SELECT * FROM territories WHERE id = :id")
    suspend fun get(id: String): TerritoryEntity?

    @Query("SELECT * FROM territories WHERE id = :id")
    fun observe(id: String): Flow<TerritoryEntity?>

    @Upsert
    suspend fun upsert(territory: TerritoryEntity)

    @Upsert
    suspend fun upsertAll(territories: List<TerritoryEntity>)

    @Query("UPDATE territories SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM territories WHERE id = :id")
    suspend fun hardDelete(id: String)
}
