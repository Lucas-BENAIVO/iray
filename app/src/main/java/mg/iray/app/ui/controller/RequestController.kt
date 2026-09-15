package mg.iray.app.ui.controller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mg.iray.app.di.ServiceLocator
import mg.iray.app.entity.RequestEntity
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.worker.enqueueImmediateSync
import mg.iray.app.worker.enqueueMediaUpload
import java.util.Calendar
import java.util.UUID

/**
 * Contrôleur demandes (démarches) + signalements.
 * Centralise le transfert UI → Room/Firestore (pattern controller).
 */
class RequestController(application: Application) : AndroidViewModel(application) {

    private val requestRepo = ServiceLocator.requestRepository()
    private val signalementRepo = ServiceLocator.signalementRepository()
    private val mediaRepo = ServiceLocator.mediaRepository()

    val requests: StateFlow<List<RequestEntity>> = requestRepo
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val signalements: StateFlow<List<SignalementEntity>> = signalementRepo
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _lastRequestRef = MutableStateFlow("")
    val lastRequestRef: StateFlow<String> = _lastRequestRef.asStateFlow()

    private val _lastSignalementRef = MutableStateFlow("")
    val lastSignalementRef: StateFlow<String> = _lastSignalementRef.asStateFlow()

    /** Brouillon formulaire démarche — clés = champs backend [RequestEntity.formData]. */
    private val _draftFormData = MutableStateFlow<Map<String, Any>>(emptyMap())
    val draftFormData: StateFlow<Map<String, Any>> = _draftFormData.asStateFlow()

    fun setDraftFormData(data: Map<String, Any>) {
        _draftFormData.value = data
    }

    fun submitRequest(
        procedureId: String,
        documentNames: List<String>,
        territoryId: String = "",
        onDone: (RequestEntity) -> Unit = {},
    ) {
        viewModelScope.launch {
            val entity = requestRepo.submit(
                RequestEntity(
                    id = UUID.randomUUID().toString(),
                    procedureId = procedureId,
                    status = "SUBMITTED",
                    formData = _draftFormData.value,
                    territoryId = territoryId,
                    documents = documentNames,
                    createdAt = System.currentTimeMillis(),
                ),
            )
            _lastRequestRef.value = entity.referenceNumber
            _draftFormData.value = emptyMap()
            enqueueImmediateSync(getApplication())
            onDone(entity)
        }
    }

    fun submitSignalement(
        category: String,
        subcategory: String,
        description: String,
        zoneLabel: String,
        photoUris: List<String>,
        onDone: (SignalementEntity) -> Unit = {},
    ) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val reference = "SIG-%d-%06d".format(
                Calendar.getInstance().get(Calendar.YEAR),
                (0..999999).random(),
            )
            val mediaIds = photoUris.mapNotNull { uri ->
                runCatching {
                    mediaRepo.saveLocally(
                        uri = uri,
                        mimeType = "image/*",
                        ownerType = "SIGNALEMENT",
                        ownerId = id,
                    ).id
                }.getOrNull()
            }
            val entity = SignalementEntity(
                id = id,
                category = category,
                subcategory = subcategory,
                description = description,
                zoneLabel = zoneLabel,
                photoMediaIds = mediaIds,
                referenceNumber = reference,
                status = "RECEIVED",
            )
            signalementRepo.save(entity)
            _lastSignalementRef.value = reference
            enqueueImmediateSync(getApplication())
            enqueueMediaUpload(getApplication())
            onDone(entity)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return RequestController(app) as T
            }
        }
    }
}
