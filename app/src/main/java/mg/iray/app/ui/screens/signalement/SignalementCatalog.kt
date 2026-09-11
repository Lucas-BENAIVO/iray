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
 * Catalogue signalement — source unique : catégories + sous-catégories.
 *
 * Best practice dynamique : UN écran sous-catégories alimenté par [get].
 * Sous-catégories de démo — à remplacer par le référentiel officiel.
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
                "Coupure d’électricité",
                "Câble dangereux",
                "Problème de compteur",
                "Poteau endommagé",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "eau",
            labelRes = R.string.signalement_cat_eau,
            icon = Icons.Filled.WaterDrop,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Coupure d’eau",
                "Fuite d’eau",
                "Tuyau endommagé",
                "Problème de pression",
                "Qualité de l’eau",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "routes",
            labelRes = R.string.signalement_cat_routes,
            icon = Icons.Filled.Construction,
            subcategories = listOf(
                "Nid-de-poule",
                "Route bloquée",
                "Pont endommagé",
                "Route dégradée",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "eclairage",
            labelRes = R.string.signalement_cat_eclairage,
            icon = Icons.Filled.Lightbulb,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Lampadaire en panne",
                "Poteau endommagé",
                "Zone non éclairée",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "proprete",
            labelRes = R.string.signalement_cat_proprete,
            icon = Icons.Filled.Delete,
            subcategories = listOf(
                "Dépotoir sauvage",
                "Bac non collecté",
                "Déchets encombrants",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "inondation",
            labelRes = R.string.signalement_cat_inondation,
            icon = Icons.Filled.Waves,
            priority = SignalementPriority.HIGH,
            subcategories = listOf(
                "Zone inondée",
                "Canal bouché",
                "Évacuation insuffisante",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "sante",
            labelRes = R.string.signalement_cat_sante,
            icon = Icons.Filled.MedicalServices,
            priority = SignalementPriority.HIGH,
            subcategories = listOf(
                "Centre fermé",
                "Manque de médicaments",
                "Insalubrité",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "infrastructures",
            labelRes = R.string.signalement_cat_infrastructures,
            icon = Icons.Filled.Business,
            priority = SignalementPriority.MEDIUM,
            subcategories = listOf(
                "Bâtiment dégradé",
                "Marché insalubre",
                "École endommagée",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "environnement",
            labelRes = R.string.signalement_cat_environnement,
            icon = Icons.Filled.Eco,
            subcategories = listOf(
                "Abattage d’arbres",
                "Pollution",
                "Feu de brousse",
                "Autre",
            ),
        ),
        SignalementCategoryInfo(
            id = "autre",
            labelRes = R.string.signalement_cat_autre,
            icon = Icons.Filled.MoreHoriz,
            subcategories = listOf("Autre"),
        ),
    )

    fun get(categoryId: String): SignalementCategoryInfo =
        categories.firstOrNull { it.id == categoryId } ?: categories.last()
}
