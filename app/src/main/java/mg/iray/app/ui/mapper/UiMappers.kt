package mg.iray.app.ui.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import mg.iray.app.entity.RequestEntity
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.ui.screens.demarcheDetail.DemarcheDetailsCatalog
import mg.iray.app.ui.screens.mesDemarches.MesDemarcheItem
import mg.iray.app.ui.screens.mesDemarches.MesDemarcheStatus
import mg.iray.app.ui.screens.mesSignalements.MesSignalementItem
import mg.iray.app.ui.screens.mesSignalements.MesSignalementStatus
import mg.iray.app.ui.screens.signalement.SignalementCatalog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRENCH)

fun RequestEntity.toMesDemarcheItem(): MesDemarcheItem {
    val details = DemarcheDetailsCatalog.get(procedureId)
    return MesDemarcheItem(
        id = id,
        dossierNumber = referenceNumber.ifBlank { id.take(8).uppercase() },
        title = details.title,
        service = details.service,
        date = dateFormat.format(Date(createdAt)),
        status = when (status.uppercase()) {
            "COMPLETED", "DONE" -> MesDemarcheStatus.DONE
            "ONGOING", "IN_PROGRESS", "PROCESSING" -> MesDemarcheStatus.ONGOING
            else -> MesDemarcheStatus.RECEIVED
        },
        icon = procedureIcon(procedureId),
    )
}

fun SignalementEntity.toMesSignalementItem(): MesSignalementItem {
    val label = SignalementCatalog.categories
        .find { it.id == category }
        ?.let { /* labelRes resolved in UI if needed */ subcategory.ifBlank { category } }
        ?: subcategory.ifBlank { category }
    return MesSignalementItem(
        id = id,
        dossierNumber = referenceNumber.ifBlank { id.take(8).uppercase() },
        title = label,
        body = description,
        date = dateFormat.format(Date(createdAt)),
        status = when (status.uppercase()) {
            "RESOLVED", "CLOSED", "DONE" -> MesSignalementStatus.RESOLVED
            "ONGOING", "IN_PROGRESS" -> MesSignalementStatus.ONGOING
            else -> MesSignalementStatus.RECEIVED
        },
        icon = signalementIcon(category),
    )
}

private fun procedureIcon(procedureId: String): ImageVector = when (procedureId) {
    "certificat-residence" -> Icons.Filled.Home
    "acte-naissance" -> Icons.Filled.Description
    "cin" -> Icons.Filled.CreditCard
    else -> Icons.Filled.AssignmentTurnedIn
}

private fun signalementIcon(categoryId: String): ImageVector = when (categoryId) {
    "eclairage", "electricite" -> Icons.Filled.Lightbulb
    "proprete", "dechets" -> Icons.Filled.Delete
    "routes", "voirie" -> Icons.Filled.Warning
    else -> Icons.Filled.Report
}
