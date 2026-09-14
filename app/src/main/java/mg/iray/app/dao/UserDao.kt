package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun observeUser(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirst(): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun get(uid: String): UserEntity?

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun delete(uid: String)

    @Query("UPDATE users SET isSynced = 1, pendingOperation = NULL WHERE uid = :uid")
    suspend fun markSynced(uid: String)

    @Query("UPDATE users SET uid = :newUid WHERE uid = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}