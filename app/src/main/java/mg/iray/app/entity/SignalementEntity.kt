package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signalements")
data class SignalementEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val category: String = "",
    val subcategory: String = "",
    val description: String = "",
    val zoneLabel: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoMediaIds: List<String> = emptyList(),
    /** Référence affichée (ex. SIG-2026-000123). */
    val referenceNumber: String = "",
    /** RECEIVED | ONGOING | RESOLVED */
    val status: String = "RECEIVED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val pendingOperation: String? = null
)
