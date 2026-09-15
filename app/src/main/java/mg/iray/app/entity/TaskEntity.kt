package mg.iray.app.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,          // UUID généré côté client
    val userId: String = "",             // uid Firebase propriétaire de la tâche
    val title: String,
    val isDone: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),

    // Champs clés pour l'offline-first
    val isSynced: Boolean = false,       // pas encore envoyé à Firestore
    val isDeleted: Boolean = false,      // soft delete (pour propager la suppression)
    val pendingOperation: String? = null // "CREATE" | "UPDATE" | "DELETE" | null
)