package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val procedureId: String = "",
    val referenceNumber: String = "",
    val status: String = "SUBMITTED",
    val formData: Map<String, Any> = emptyMap(),
    val territoryId: String = "",
    val documents: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val pendingOperation: String? = null
)
