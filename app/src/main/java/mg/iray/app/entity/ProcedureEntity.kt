package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "procedures")
data class ProcedureEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val requiredDocuments: List<String> = emptyList(),
    val territoryType: String = "",
    val estimatedProcessingDays: Int = 0,
    val isFree: Boolean = true,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val pendingOperation: String? = null
)
