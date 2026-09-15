package mg.iray.app.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import mg.iray.app.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE uid = :uid")
    fun observeProfile(uid: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE uid = :uid")
    suspend fun get(uid: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles")
    suspend fun getAll(): List<UserProfileEntity>

    @Query("SELECT * FROM user_profiles WHERE pendingOperation IS NOT NULL")
    suspend fun getPending(): List<UserProfileEntity>

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    @Upsert
    suspend fun upsertAll(profiles: List<UserProfileEntity>)

    @Query("UPDATE user_profiles SET isSynced = 1, pendingOperation = NULL WHERE uid = :uid")
    suspend fun markSynced(uid: String)

    @Query("UPDATE user_profiles SET uid = :newUid WHERE uid = :oldUid")
    suspend fun rewriteUid(oldUid: String, newUid: String)
}