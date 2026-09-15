package mg.iray.app.ui.validation

import java.util.Calendar

/**
 * Validation des champs citoyen — formats Madagascar / JJ/MM/AAAA.
 */
object FormValidators {

    private val nameRegex = Regex("^[\\p{L} '\\-]{2,40}$")
    /** +261 3X XX XXX XX ou 03X XX XXX XX (espaces optionnels). */
    private val phoneRegex = Regex(
        "^(\\+261|0)3[2-478]\\d{7}$",
    )

    fun normalizePhone(raw: String): String =
        raw.filter { it.isDigit() || it == '+' }

    fun isValidName(value: String): Boolean =
        nameRegex.matches(value.trim())

    fun isValidPhone(value: String): Boolean {
        val compact = normalizePhone(value)
        return phoneRegex.matches(compact)
    }

    /**
     * Accepte uniquement JJ/MM/AAAA, date réelle, âge entre 1 et 120 ans.
     */
    fun isValidBirthdate(value: String): Boolean {
        val parts = value.trim().split("/")
        if (parts.size != 3) return false
        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false
        if (parts[0].length != 2 || parts[1].length != 2 || parts[2].length != 4) return false
        if (month !in 1..12 || day !in 1..31) return false

        return try {
            val cal = Calendar.getInstance().apply {
                isLenient = false
                set(year, month - 1, day)
            }
            cal.time // force validation (throws if invalid day/month)
            val now = Calendar.getInstance()
            if (cal.after(now)) return false
            val age = now.get(Calendar.YEAR) - year -
                if (now.get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR)) 1 else 0
            age in 1..120
        } catch (_: Exception) {
            false
        }
    }

    /** Masque saisie date : digits → JJ/MM/AAAA. */
    fun formatBirthdateInput(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(8)
        return buildString {
            digits.forEachIndexed { index, c ->
                if (index == 2 || index == 4) append('/')
                append(c)
            }
        }
    }

    /** Masque téléphone léger : garde + et chiffres, limite longueur. */
    fun formatPhoneInput(raw: String): String {
        val hasPlus = raw.trimStart().startsWith("+")
        val digits = raw.filter { it.isDigit() }.take(12)
        return if (hasPlus) "+$digits" else digits
    }
}
