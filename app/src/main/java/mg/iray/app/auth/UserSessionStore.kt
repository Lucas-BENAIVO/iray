package mg.iray.app.auth

import android.content.Context

/**
 * Persiste l'uid localement (SharedPreferences) pour rester disponible hors-ligne :
 * c'est ce qui garantit un uid unique et stable dès la première ouverture.
 */
interface UserSessionStore {
    fun saveUid(uid: String)
    fun getUid(): String?
    fun clear()
}

class SharedPrefsUserSession(context: Context) : UserSessionStore {
    private val prefs = context.applicationContext
        .getSharedPreferences("iray_session", Context.MODE_PRIVATE)

    override fun saveUid(uid: String) {
        prefs.edit().putString(KEY, uid).apply()
    }

    override fun getUid(): String? = prefs.getString(KEY, null)

    override fun clear() {
        prefs.edit().remove(KEY).apply()
    }

    private companion object {
        const val KEY = "uid"
    }
}