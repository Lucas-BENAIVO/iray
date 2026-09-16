package mg.iray.app.ui.screens.demarcheDetail

/**
 * Antsipirian’ny asa ara-pitantanana - écran dynamique unique.
 * Données de démo en malagasy.
 */
data class DemarcheDetails(
    val id: String,
    val title: String,
    val service: String,
    val documents: List<String>,
    val delay: String,
    val fees: String,
    val asksBirthPlace: Boolean = false,
)

object DemarcheDetailsCatalog {

    fun get(id: String): DemarcheDetails = byId[id] ?: fallback(id)

    private val byId: Map<String, DemarcheDetails> = listOf(
        DemarcheDetails(
            id = "certificat-residence",
            title = "Taratasy fanamarinana ny fonenana",
            service = "Fitantanana eo an-toerana",
            documents = listOf(
                "Taratasy fanamarinan-toetra (CIN na pasipaoro)",
                "Porofon’ny fonenana",
                "Taratasim-pangatahana (an-tserasera)",
            ),
            delay = "2 ka hatramin’ny 7 andro fiasana",
            fees = "Maimaim-poana na araka ny kaominina",
        ),
        DemarcheDetails(
            id = "acte-naissance",
            title = "Soratra nahaterahana",
            service = "Sora-piainanana",
            documents = listOf(
                "Taratasy fanamarinan-toetra (CIN na pasipaoro)",
                "Boky fianakaviana (raha misy)",
                "Taratasim-pangatahana (an-tserasera)",
            ),
            delay = "2 ka hatramin’ny 5 andro fiasana",
            fees = "Araka ny kaominina",
        ),
        DemarcheDetails(
            id = "cin",
            title = "Karam-pirenen’ny maha-olona (CIN)",
            service = "Fitsarana / Sora-piainanana",
            documents = listOf(
                "Sary mombamomba",
                "Soratra nahaterahana",
                "Taratasim-pangatahana (an-tserasera)",
            ),
            delay = "7 ka hatramin’ny 15 andro fiasana",
            fees = "Araka ny kaominina",
            asksBirthPlace = true,
        ),
        DemarcheDetails(
            id = "autres",
            title = "Asa hafa",
            service = "Varavarana tokana",
            documents = listOf(
                "Taratasy fanamarinan-toetra (CIN na pasipaoro)",
                "Taratasim-pangatahana (an-tserasera)",
            ),
            delay = "Araka ny asa",
            fees = "Araka ny kaominina",
        ),
    ).associateBy { it.id }

    private fun fallback(id: String) = DemarcheDetails(
        id = id,
        title = id,
        service = "Varavarana tokana",
        documents = listOf("Taratasy fanamarinan-toetra (CIN na pasipaoro)"),
        delay = "Araka ny asa",
        fees = "Araka ny kaominina",
    )
}
