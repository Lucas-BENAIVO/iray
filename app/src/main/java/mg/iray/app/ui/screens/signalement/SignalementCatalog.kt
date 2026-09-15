package mg.iray.app.ui.screens.signalement

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import mg.iray.app.R

/**
 * Catalogue tatitra — sokajy + zana-tsokajy (malagasy).
 */
data class SignalementCategoryInfo(
    val id: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val subcategories: List<String>,
    val priority: SignalementPriority = SignalementPriority.LOW,
)

enum class SignalementPriority {
    HIGH,
    MEDIUM,
    LOW,
}

object SignalementCatalog {

    val categories: List<SignalementCategoryInfo> = listOf(
        SignalementCategoryInfo(
            id = "electricite",
            labelRes = R.string.signalement_cat_electricite,
            icon = Icons.Filled.Bolt,
            priority = SignalementPriority.HIGH,
            subcategories = listOf(
                "Tapaka ny herinaratra",
                "Tady mampidi-doza",
                "Olana amin’ny compteur",
                "Andry simba",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "eau",
            labelRes = R.string.signalement_cat_eau,
            icon = Icons.Filled.WaterDrop,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Tapaka ny rano",
                "Mitarika ny rano",
                "Fantsona simba",
                "Olana amin’ny tsindry",
                "Kalitaon’ny rano",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "routes",
            labelRes = R.string.signalement_cat_routes,
            icon = Icons.Filled.Construction,
            subcategories = listOf(
                "Lavaka amin’ny lalana",
                "Lalana sakana",
                "Tetezana simba",
                "Lalana simba",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "eclairage",
            labelRes = R.string.signalement_cat_eclairage,
            icon = Icons.Filled.Lightbulb,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Jiro tsy mandeha",
                "Andry simba",
                "Faritra tsy misy jiro",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "proprete",
            labelRes = R.string.signalement_cat_proprete,
            icon = Icons.Filled.Delete,
            subcategories = listOf(
                "Fako tsy ara-dalàna",
                "Daba tsy nalaina",
                "Fako lehibe",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "inondation",
            labelRes = R.string.signalement_cat_inondation,
            icon = Icons.Filled.Waves,
            priority = SignalementPriority.HIGH,
            subcategories = listOf(
                "Faritra tondraka",
                "Lakandrano sakana",
                "Tsy ampy ny fivoahan-drano",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "sante",
            labelRes = R.string.signalement_cat_sante,
            icon = Icons.Filled.MedicalServices,
            priority = SignalementPriority.HIGH,
            subcategories = listOf(
                "Tobim-pahasalamana mihidy",
                "Tsy ampy fanafody",
                "Tsy madio",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "infrastructures",
            labelRes = R.string.signalement_cat_infrastructures,
            icon = Icons.Filled.Business,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Trano simba",
                "Tsena tsy madio",
                "Sekoly simba",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "environnement",
            labelRes = R.string.signalement_cat_environnement,
            icon = Icons.Filled.Eco,
            subcategories = listOf(
                "Fianjeran’ny hazo",
                "Loto",
                "Afo an-tsaha",
                "Hafa",
            ),
        ),
        SignalementCategoryInfo(
            id = "autre",
            labelRes = R.string.signalement_cat_autre,
            icon = Icons.Filled.MoreHoriz,
            subcategories = listOf("Hafa"),
        ),
    )

    fun get(categoryId: String): SignalementCategoryInfo =
        categories.firstOrNull { it.id == categoryId } ?: categories.last()
}
