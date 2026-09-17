package mg.iray.app.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persiste l'uid localement (SharedPreferences) pour rester disponible hors-ligne :
 * c'est ce qui garantit un uid unique et stable dès la première ouverture.
 */
interface UserSessionStore {
    fun saveUid(uid: String)
    fun getUid(): String?
    fun observeUid(): StateFlow<String?>
    fun clear()
}

class SharedPrefsUserSession(context: Context) : UserSessionStore {
    private val prefs = context.applicationContext
        .getSharedPreferences("iray_session", Context.MODE_PRIVATE)

    private val _uid = MutableStateFlow(prefs.getString(KEY, null))
    override fun observeUid(): StateFlow<String?> = _uid.asStateFlow()

    override fun saveUid(uid: String) {
        prefs.edit().putString(KEY, uid).apply()
        _uid.value = uid
    }

    override fun getUid(): String? = _uid.value ?: prefs.getString(KEY, null)

    override fun clear() {
        prefs.edit().remove(KEY).apply()
        _uid.value = null
    }

    private companion object {
        const val KEY = "uid"
    }
}
