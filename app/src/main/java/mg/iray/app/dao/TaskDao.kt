package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE pendingOperation IS NOT NULL")
    suspend fun getPendingTasks(): List<TaskEntity>

    @Upsert
    suspend fun upsert(task: TaskEntity)

    @Upsert
    suspend fun upsertAll(tasks: List<TaskEntity>)

    @Query("UPDATE tasks SET isSynced = 1, pendingOperation = NULL WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun hardDelete(id: String)
}