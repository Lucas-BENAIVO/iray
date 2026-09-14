package mg.iray.app.auth

interface AuthSource {
    fun currentUserId(): String?

    fun isAnonymous(): Boolean

    /** Sign-in anonyme Firebase : retourne un uid stable pour l'appareil. */
    suspend fun signInAnonymously(): String

    /** Lie l'identité (email/mot de passe) à l'utilisateur anonyme existant. */
    suspend fun linkWithEmail(email: String, password: String): String
}