package mg.iray.app.ui.controller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mg.iray.app.di.ServiceLocator
import mg.iray.app.entity.UserProfileEntity
import mg.iray.app.worker.enqueueImmediateSync

/**
 * Contrôleur profil - unique source de vérité Room / sync.
 * Les infos citoyen sont assemblées avec l’uid de session.
 */
class ProfileController(application: Application) : AndroidViewModel(application) {

    private val session = ServiceLocator.session

    private val _ready = MutableStateFlow(false)
    /** true après uid + 1ʳᵉ lecture Room. */
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    val profile: StateFlow<UserProfileEntity?> = flow {
        // Garantit l’uid avant d’observer le profil (même uid à chaque ouverture).
        ServiceLocator.authRepository().ensureUserId()
        emitAll(ServiceLocator.userProfileRepository().observeProfile())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val hasProfile: StateFlow<Boolean> = profile
        .map {
            it != null &&
                it.firstName.isNotBlank() &&
                it.lastName.isNotBlank() &&
                it.commune.isNotBlank() &&
                it.fokontany.isNotBlank()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            profile.first()
            _ready.value = true
            val uid = session.getUid()
            val p = profile.value
            Log.d(
                TAG,
                "bootstrap uid=$uid hasProfile=${p != null && p.firstName.isNotBlank()} " +
                    "name=${p?.firstName.orEmpty()} ${p?.lastName.orEmpty()}",
            )
        }
    }

    fun saveIdentity(
        firstName: String,
        lastName: String,
        phone: String,
        birthdate: String,
        onDone: () -> Unit = {},
    ) {
        viewModelScope.launch {
            val uid = session.getUid().orEmpty()
            val existing = profile.value
            ServiceLocator.userProfileRepository().saveProfile(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                phone = phone.trim(),
                birthdate = birthdate.trim(),
                commune = existing?.commune.orEmpty(),
                fokontany = existing?.fokontany.orEmpty(),
            )
            Log.d(TAG, "profil identité sauvé pour uid=$uid ($firstName $lastName)")
            enqueueImmediateSync(getApplication())
            onDone()
        }
    }

    fun saveZone(
        commune: String,
        fokontany: String,
        onDone: () -> Unit = {},
    ) {
        viewModelScope.launch {
            val uid = session.getUid().orEmpty()
            val existing = profile.value
            ServiceLocator.userProfileRepository().saveProfile(
                firstName = existing?.firstName.orEmpty(),
                lastName = existing?.lastName.orEmpty(),
                phone = existing?.phone.orEmpty(),
                birthdate = existing?.birthdate.orEmpty(),
                commune = commune.trim(),
                fokontany = fokontany.trim(),
            )
            Log.d(TAG, "profil zone sauvé pour uid=$uid ($commune, $fokontany)")
            enqueueImmediateSync(getApplication())
            onDone()
        }
    }

    companion object {
        private const val TAG = "IrayProfile"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ProfileController(app) as T
            }
        }
    }
}
