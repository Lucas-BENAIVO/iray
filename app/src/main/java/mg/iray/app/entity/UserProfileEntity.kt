package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    /** JJ/MM/AAAA — aligné sur le formulaire profil UI. */
    val birthdate: String = "",
    val commune: String = "",
    val fokontany: String = "",
    val avatarLocalUri: String? = null,
    val avatarRemoteUrl: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val pendingOperation: String? = null
)
