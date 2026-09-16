package mg.iray.app.ui.controller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mg.iray.app.di.ServiceLocator
import mg.iray.app.entity.TerritoryEntity

/** Types du référentiel Firestore `territories`. */
object TerritoryType {
    const val REGION = "REGION"
    const val DISTRICT = "DISTRICT"
    const val COMMUNE = "COMMUNE"
    const val ARRONDISSEMENT = "ARRONDISSEMENT"
    const val FOKONTANY = "FOKONTANY"
}

data class ZoneUiSelection(
    val regionId: String? = null,
    val districtId: String? = null,
    val communeId: String? = null,
    val arrondissementId: String? = null,
    val fokontanyId: String? = null,
)

/**
 * Cascade REGION → DISTRICT → COMMUNE → (ARRONDISSEMENT?) → FOKONTANY
 * Lecture Room (offline-first). Seed assets si vide ; sync réseau en fond.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ZoneController(application: Application) : AndroidViewModel(application) {

    private val repo = ServiceLocator.territoryRepository()

    private val _selection = MutableStateFlow(ZoneUiSelection())
    val selection: StateFlow<ZoneUiSelection> = _selection.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    val regions: StateFlow<List<TerritoryEntity>> = combine(
        repo.observeByType(TerritoryType.REGION),
        repo.observeRoots(),
    ) { byType, roots ->
        byType.ifEmpty { roots }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val districts: StateFlow<List<TerritoryEntity>> = _selection
        .flatMapLatest { sel ->
            val id = sel.regionId ?: return@flatMapLatest flowOf(emptyList())
            repo.observeByParent(id)
        }
        .map { list -> list.filterByTypeOrAll(TerritoryType.DISTRICT) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val communes: StateFlow<List<TerritoryEntity>> = _selection
        .flatMapLatest { sel ->
            val id = sel.districtId ?: return@flatMapLatest flowOf(emptyList())
            repo.observeByParent(id)
        }
        .map { list -> list.filterByTypeOrAll(TerritoryType.COMMUNE) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val communeChildren: StateFlow<List<TerritoryEntity>> = _selection
        .flatMapLatest { sel ->
            val id = sel.communeId ?: return@flatMapLatest flowOf(emptyList())
            repo.observeByParent(id)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val arrondissements: StateFlow<List<TerritoryEntity>> = communeChildren
        .map { list -> list.filter { it.type.equals(TerritoryType.ARRONDISSEMENT, ignoreCase = true) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val showArrondissement: StateFlow<Boolean> = arrondissements
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val fokontanys: StateFlow<List<TerritoryEntity>> = _selection
        .flatMapLatest { sel ->
            when {
                sel.arrondissementId != null ->
                    repo.observeByParent(sel.arrondissementId!!)
                        .map { list -> list.filterByTypeOrAll(TerritoryType.FOKONTANY) }
                sel.communeId != null ->
                    repo.observeByParent(sel.communeId!!).map { children ->
                        val hasArrondissement = children.any {
                            it.type.equals(TerritoryType.ARRONDISSEMENT, ignoreCase = true)
                        }
                        if (hasArrondissement) emptyList()
                        else children.filterByTypeOrAll(TerritoryType.FOKONTANY)
                    }
                else -> flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val canContinue: StateFlow<Boolean> = _selection
        .map { it.fokontanyId != null && it.communeId != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        refresh()
    }

    /**
     * Offline-first : seed Room tout de suite, sync Firestore en arrière-plan.
     */
    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _loadError.value = false
            runCatching { repo.ensureLocalReferentiel() }
            val count = runCatching { repo.count() }.getOrDefault(0)
            _loadError.value = count == 0
            _loading.value = false

            launch {
                runCatching { repo.pullRemoteChanges() }
                    .onFailure { Log.w(TAG, "territory pull skipped (offline): ${it.message}") }
            }
        }
    }

    fun selectRegion(id: String) {
        _selection.value = ZoneUiSelection(regionId = id)
    }

    fun selectDistrict(id: String) {
        _selection.update {
            it.copy(
                districtId = id,
                communeId = null,
                arrondissementId = null,
                fokontanyId = null,
            )
        }
    }

    fun selectCommune(id: String) {
        _selection.update {
            it.copy(
                communeId = id,
                arrondissementId = null,
                fokontanyId = null,
            )
        }
    }

    fun selectArrondissement(id: String) {
        _selection.update {
            it.copy(arrondissementId = id, fokontanyId = null)
        }
    }

    fun selectFokontany(id: String) {
        _selection.update { it.copy(fokontanyId = id) }
    }

    fun nameOf(list: List<TerritoryEntity>, id: String?): String =
        list.firstOrNull { it.id == id }?.name.orEmpty()

    companion object {
        private const val TAG = "IrayZone"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ZoneController(app) as T
            }
        }
    }
}

/** Si le serveur tague le type, on filtre ; sinon on garde tous les enfants du parent. */
private fun List<TerritoryEntity>.filterByTypeOrAll(type: String): List<TerritoryEntity> {
    val matched = filter { it.type.equals(type, ignoreCase = true) }
    return matched.ifEmpty { this }
}
