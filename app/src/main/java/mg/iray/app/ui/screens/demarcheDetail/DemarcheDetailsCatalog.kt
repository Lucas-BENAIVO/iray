package mg.iray.app.ui.screens.demarcheDetail

/**
 * Détail d’une démarche — best practice dynamique : UN seul écran,
 * alimenté par [DemarcheDetails] selon l’id cliqué.
 *
 * Données de démo — à remplacer par le repository / l’API officielle.
 */
data class DemarcheDetails(
    val id: String,
    val title: String,
    val service: String,
    val documents: List<String>,
    val delay: String,
    val fees: String,
    /** La CIN demande en plus le lieu de naissance dans le formulaire. */
    val asksBirthPlace: Boolean = false,
)

object DemarcheDetailsCatalog {

    fun get(id: String): DemarcheDetails = byId[id] ?: fallback(id)

    private val byId: Map<String, DemarcheDetails> = listOf(
        DemarcheDetails(
            id = "acte-naissance",
            title = "Acte de naissance",
            service = "État civil",
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Livret de famille (si disponible)",
                "Formulaire de demande (en ligne)",
            ),
            delay = "2 à 5 jours ouvrés",
            fees = "Selon la commune",
        ),
        DemarcheDetails(
            id = "certificat-residence",
            title = "Certificat de résidence",
            service = "Administration locale",
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Justificatif de domicile",
                "Formulaire de demande (en ligne)",
            ),
            delay = "2 à 7 jours ouvrés",
            fees = "Gratuit ou selon la commune",
        ),
        DemarcheDetails(
            id = "copie-acte-naissance",
            title = "Copie d’acte de naissance",
            service = "État civil",
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Livret de famille (si disponible)",
                "Formulaire de demande (en ligne)",
            ),
            delay = "2 à 5 jours ouvrés",
            fees = "Selon la commune",
        ),
        DemarcheDetails(
            id = "cin",
            title = "Carte nationale d’identité (CIN)",
            service = "Justice / État civil",
            documents = listOf(
                "Photo d’identité",
                "Acte de naissance",
                "Formulaire de demande (en ligne)",
            ),
            delay = "7 à 15 jours ouvrés",
            fees = "Selon la commune",
            asksBirthPlace = true,
        ),
        DemarcheDetails(
            id = "certificat-residence-2",
            title = "Certificat de résidence",
            service = "Administration locale",
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Justificatif de domicile",
                "Formulaire de demande (en ligne)",
            ),
            delay = "2 à 7 jours ouvrés",
            fees = "Gratuit ou selon la commune",
        ),
        DemarcheDetails(
            id = "autres",
            title = "Autres démarches",
            service = "Guichet unique",
            documents = listOf(
                "Pièce d’identité (CIN ou passeport)",
                "Formulaire de demande (en ligne)",
            ),
            delay = "Selon la démarche",
            fees = "Selon la commune",
        ),
    ).associateBy { it.id }

    private fun fallback(id: String) = DemarcheDetails(
        id = id,
        title = id,
        service = "Guichet unique",
        documents = listOf("Pièce d’identité (CIN ou passeport)"),
        delay = "Selon la démarche",
        fees = "Selon la commune",
    )
}
