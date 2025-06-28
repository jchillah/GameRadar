package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.ui.states.SearchUiState
import de.syntax_institut.androidabschlussprojekt.utils.NetworkUtils
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import androidx.paging.cachedIn
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.local.models.SearchParams
import java.lang.ref.WeakReference

/**
 * ViewModel für die Suche mit Offline-Support.
 * Folgt MVVM-Pattern und Clean Code Prinzipien.
 */
class SearchViewModel(
    private val repo: GameRepository,
    context: Context
) : ViewModel() {

    // WeakReference um Context-Leak zu vermeiden
    private val contextRef = WeakReference(context)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    // Paging-Flow für die UI
    private val _pagingFlow = MutableStateFlow<PagingData<Game>>(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Game>> = _pagingFlow.asStateFlow()

    private val _searchParams = MutableStateFlow(SearchParams())
    
    // Aktueller Suchtext für automatische Neuausführung
    private var currentSearchQuery: String = ""
    
    // Netzwerkstatus
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()
    
    // Cache-Informationen
    private val _cacheSize = MutableStateFlow(0)
    val cacheSize: StateFlow<Int> = _cacheSize.asStateFlow()

    init {
        initializeNetworkMonitoring()
        initializeCacheMonitoring()
    }

    private fun initializeNetworkMonitoring() {
        viewModelScope.launch {
            contextRef.get()?.let { context ->
                NetworkUtils.observeNetworkStatus(context).collect { isOnline ->
                    _isOffline.value = !isOnline
                    Log.d("SearchViewModel", "Netzwerkstatus geändert: ${if (isOnline) "Online" else "Offline"}")
                }
            }
        }
    }

    private fun initializeCacheMonitoring() {
        viewModelScope.launch {
            while (true) {
                try {
                    _cacheSize.value = repo.getCacheSize()
                } catch (e: Exception) {
                    Log.e("SearchViewModel", "Fehler beim Abrufen der Cache-Größe", e)
                }
                delay(30000) // Alle 30 Sekunden aktualisieren
            }
        }
    }

    fun loadPlatforms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPlatforms = true, platformsError = null) }
            try {
                val platformResponse = repo.getPlatforms()
                if (platformResponse is Resource.Success) {
                    _uiState.update { it.copy(platforms = platformResponse.data ?: emptyList(), isLoadingPlatforms = false) }
                } else {
                    _uiState.update { 
                        it.copy(
                            platformsError = platformResponse.message ?: "Unbekannter Fehler",
                            isLoadingPlatforms = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        platformsError = "Fehler beim Laden der Plattformen: ${e.localizedMessage}",
                        isLoadingPlatforms = false
                    ) 
                }
            }
        }
    }

    fun loadGenres() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGenres = true, genresError = null) }
            try {
                val genreResponse = repo.getGenres()
                if (genreResponse is Resource.Success) {
                    _uiState.update { it.copy(genres = genreResponse.data ?: emptyList(), isLoadingGenres = false) }
                } else {
                    _uiState.update { 
                        it.copy(
                            genresError = genreResponse.message ?: "Unbekannter Fehler",
                            isLoadingGenres = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        genresError = "Fehler beim Laden der Genres: ${e.localizedMessage}",
                        isLoadingGenres = false
                    ) 
                }
            }
        }
    }

    fun search(query: String) {
        Log.d("SearchViewModel", "Paging-Search gestartet mit Query: $query")
        currentSearchQuery = query
        val state = _uiState.value
        val platformIds = state.selectedPlatforms.joinToString(",")
        val genreIds = state.selectedGenres.joinToString(",")
        val ordering = state.ordering
        val rating = if (state.rating > 0f) state.rating else null
        _uiState.update { it.copy(hasSearched = true) }
        _searchParams.value = SearchParams(
            query = query,
            platforms = if (platformIds.isNotBlank()) platformIds else null,
            genres = if (genreIds.isNotBlank()) genreIds else null,
            ordering = if (ordering.isNotBlank()) ordering else null
        )
        viewModelScope.launch {
            repo.getPagedGames(
                query = _searchParams.value.query,
                platforms = _searchParams.value.platforms,
                genres = _searchParams.value.genres,
                ordering = _searchParams.value.ordering,
                rating = rating
            )
            .cachedIn(viewModelScope)
            .collect {
                _pagingFlow.value = it
            }
        }
    }

    fun updateFilters(platforms: List<String>, genres: List<String>, rating: Float) {
        _uiState.update { it.copy(
            selectedPlatforms = platforms,
            selectedGenres = genres,
            rating = rating
        )}
        
        // Automatisch die Suche neu ausführen, wenn ein Suchtext vorhanden ist
        if (currentSearchQuery.isNotBlank()) {
            search(currentSearchQuery)
        }
    }

    fun updateOrdering(ordering: String) {
        _uiState.update { it.copy(ordering = ordering) }
        
        // Automatisch die Suche neu ausführen, wenn ein Suchtext vorhanden ist
        if (currentSearchQuery.isNotBlank()) {
            search(currentSearchQuery)
        }
    }
    
    /**
     * Cache verwalten
     */
    fun clearCache() {
        viewModelScope.launch {
            try {
                repo.clearCache()
                _cacheSize.value = 0
                Log.d("SearchViewModel", "Cache erfolgreich geleert")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Fehler beim Leeren des Caches", e)
            }
        }
    }
    
    fun clearOldCache() {
        viewModelScope.launch {
            try {
                repo.clearOldCache()
                _cacheSize.value = repo.getCacheSize()
                Log.d("SearchViewModel", "Alter Cache erfolgreich geleert")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Fehler beim Leeren des alten Caches", e)
            }
        }
    }
    
    /**
     * Prüft ob Query im Cache verfügbar ist
     */
    suspend fun isQueryCached(query: String): Boolean {
        return try {
            repo.isQueryCached(query)
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Fehler beim Prüfen des Cache-Status", e)
            false
        }
    }
}
