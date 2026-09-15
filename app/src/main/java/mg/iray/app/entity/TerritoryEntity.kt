package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "territories")
data class TerritoryEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val type: String = "",
    val parentId: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val pendingOperation: String? = null
)
