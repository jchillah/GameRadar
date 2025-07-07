package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.util.*
import androidx.lifecycle.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel für die Suche mit Offline-Support.
 * Folgt MVVM-Pattern und Clean Code Prinzipien.
 */
class SearchViewModel(
    private val repo: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    // Paging-Flow für die UI
    private val _pagingFlow = MutableStateFlow<PagingData<Game>>(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Game>> = _pagingFlow.asStateFlow()

    private val _searchParams = MutableStateFlow(SearchParams())
    
    // Aktueller Suchtext für automatische Neuausführung
    private var currentSearchQuery: String = ""
    
    // Cache-Informationen
    private val _cacheSize = MutableStateFlow(0)

    init {
        // Verzögerte Initialisierung um Binder-Transaktionsfehler zu vermeiden
        viewModelScope.launch {
            delay(100) // Kurze Verzögerung für stabilen App-Start
            initializeCacheMonitoring()
        }
    }

    private fun initializeCacheMonitoring() {
        viewModelScope.launch {
            try {
                // Initiale Cache-Größe abrufen mit Verzögerung
                delay(500) // Längere Verzögerung für stabilen Start
                _cacheSize.value = repo.getCacheSize()
                _uiState.update { it.copy(lastSyncTime = System.currentTimeMillis()) }

                // Nur alle 60 Sekunden aktualisieren, nicht in einer unendlichen Schleife
                while (true) {
                    delay(60000) // 60 Sekunden warten (weniger aggressiv)
                    try {
                        _cacheSize.value = repo.getCacheSize()
                        _uiState.update { it.copy(lastSyncTime = System.currentTimeMillis()) }
                    } catch (e: Exception) {
                        Log.e("SearchViewModel", "Fehler beim Abrufen der Cache-Größe", e)
                        // Bei Fehlern nicht abbrechen, sondern weiter versuchen
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Kritischer Fehler im Cache-Monitoring", e)
                // Bei kritischen Fehlern das Monitoring stoppen
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
    }

    fun updateOrdering(ordering: String) {
        _uiState.update { it.copy(ordering = ordering) }
        // KEINE automatische Suche mehr hier!
    }

    fun resetSearch() {
        _uiState.update { it.copy(hasSearched = false) }
        _pagingFlow.value = PagingData.empty()
        currentSearchQuery = ""
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

    fun removePlatformFilter(platformId: String) {
        val newPlatforms = _uiState.value.selectedPlatforms.filterNot { it == platformId }
        _uiState.update { it.copy(selectedPlatforms = newPlatforms) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    fun removeGenreFilter(genreId: String) {
        val newGenres = _uiState.value.selectedGenres.filterNot { it == genreId }
        _uiState.update { it.copy(selectedGenres = newGenres) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    fun removeRatingFilter() {
        _uiState.update { it.copy(rating = 0f) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    fun removeOrderingFilter() {
        _uiState.update { it.copy(ordering = "") }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    fun clearAllFilters() {
        _uiState.update {
            it.copy(
                selectedPlatforms = emptyList(),
                selectedGenres = emptyList(),
                rating = 0f,
                ordering = ""
            )
        }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }
}
