package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** ViewModel für die Suchfunktionalität. */
class SearchViewModel(
    private val loadGamesUseCase: LoadGamesUseCase,
    private val getPlatformsUseCase: GetPlatformsUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val getCacheSizeUseCase: GetCacheSizeUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
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
        // Setze Custom Keys für Crashlytics-Tracking
        CrashlyticsHelper.setCustomKey("current_screen", "SearchScreen")
        CrashlyticsHelper.setCustomKey("search_initialized", true)

        // Verzögerte Initialisierung um Binder-Transaktionsfehler zu vermeiden
        viewModelScope.launch {
            delay(100) // Kurze Verzögerung für stabilen App-Start
            initializeCacheMonitoring()
        }
    }

    private fun initializeCacheMonitoring() {
        viewModelScope.launch {
            try {
                CrashlyticsHelper.setCustomKey("cache_monitoring_started", true)

                // Initiale Cache-Größe abrufen mit Verzögerung
                delay(Constants.CACHE_MONITORING_DELAY) // Längere Verzögerung für stabilen Start
                _cacheSize.value = getCacheSizeUseCase()
                _uiState.update { it.copy(lastSyncTime = System.currentTimeMillis()) }

                // Nur alle 60 Sekunden aktualisieren, nicht in einer unendlichen Schleife
                while (true) {
                    delay(
                        Constants.CACHE_MONITORING_INTERVAL
                    ) // 60 Sekunden warten (weniger aggressiv)
                    try {
                        _cacheSize.value = getCacheSizeUseCase()
                        _uiState.update { it.copy(lastSyncTime = System.currentTimeMillis()) }
                    } catch (_: Exception) {
                        CrashlyticsHelper.setCustomKey("cache_monitoring_error", true)
                        AppLogger.e("SearchViewModel", "Fehler beim Abrufen der Cache-Größe")
                        // Bei Fehlern nicht abbrechen, sondern weiter versuchen
                    }
                }
            } catch (_: Exception) {
                CrashlyticsHelper.setCustomKey("cache_monitoring_critical_error", true)
                AppLogger.e("SearchViewModel", "Kritischer Fehler im Cache-Monitoring")
                // Bei kritischen Fehlern das Monitoring stoppen
            }
        }
    }

    fun loadPlatforms() {
        viewModelScope.launch {
            CrashlyticsHelper.setCustomKey("platforms_loading_started", true)
            _uiState.update { it.copy(isLoadingPlatforms = true, platformsError = null) }
            try {
                val platformResponse = getPlatformsUseCase()
                if (platformResponse is Resource.Success) {
                    CrashlyticsHelper.setCustomKey("platforms_loading_success", true)
                    CrashlyticsHelper.setCustomKey(
                        "platforms_count",
                        platformResponse.data?.size ?: 0
                    )

                    _uiState.update {
                        it.copy(
                            platforms = platformResponse.data ?: emptyList(),
                            isLoadingPlatforms = false,
                            platformsErrorId = null
                        )
                    }
                } else {
                    CrashlyticsHelper.setCustomKey("platforms_loading_error", true)
                    CrashlyticsHelper.setCustomKey(
                        "platforms_error_message",
                        platformResponse.message ?: ""
                    )

                    val errorId = de.syntax_institut.androidabschlussprojekt.R.string.error_unknown
                    _uiState.update {
                        it.copy(platformsErrorId = errorId, isLoadingPlatforms = false)
                    }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey(
                    "platforms_loading_exception",
                    e.javaClass.simpleName
                )
                CrashlyticsHelper.setCustomKey(
                    "platforms_exception_message",
                    e.message ?: "Unknown"
                )

                val errorId =
                    de.syntax_institut.androidabschlussprojekt.R.string.error_load_platforms
                _uiState.update { it.copy(platformsErrorId = errorId, isLoadingPlatforms = false) }
            }
        }
    }

    fun loadGenres() {
        viewModelScope.launch {
            CrashlyticsHelper.setCustomKey("genres_loading_started", true)
            _uiState.update { it.copy(isLoadingGenres = true, genresError = null) }
            try {
                val genreResponse = getGenresUseCase()
                if (genreResponse is Resource.Success) {
                    CrashlyticsHelper.setCustomKey("genres_loading_success", true)
                    CrashlyticsHelper.setCustomKey("genres_count", genreResponse.data?.size ?: 0)

                    _uiState.update {
                        it.copy(
                            genres = genreResponse.data ?: emptyList(),
                            isLoadingGenres = false,
                            genresErrorId = null
                        )
                    }
                } else {
                    CrashlyticsHelper.setCustomKey("genres_loading_error", true)
                    CrashlyticsHelper.setCustomKey(
                        "genres_error_message",
                        genreResponse.message ?: ""
                    )

                    val errorId = de.syntax_institut.androidabschlussprojekt.R.string.error_unknown
                    _uiState.update { it.copy(genresErrorId = errorId, isLoadingGenres = false) }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("genres_loading_exception", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("genres_exception_message", e.message ?: "Unknown")

                val errorId = de.syntax_institut.androidabschlussprojekt.R.string.error_load_genres
                _uiState.update { it.copy(genresErrorId = errorId, isLoadingGenres = false) }
            }
        }
    }

    fun search(query: String) {
        CrashlyticsHelper.setCustomKey("search_attempted", true)
        CrashlyticsHelper.setCustomKey("search_query", query)
        CrashlyticsHelper.setCustomKey("search_query_length", query.length)

        AppLogger.d("SearchViewModel", "Paging-Search gestartet mit Query: $query")
        currentSearchQuery = query
        val state = _uiState.value
        val platformIds = state.selectedPlatforms.joinToString(",")
        val genreIds = state.selectedGenres.joinToString(",")
        val ordering = state.ordering
        val rating = if (state.rating > 0f) state.rating else null

        // Setze Custom Keys für Suchparameter
        CrashlyticsHelper.setCustomKey("search_platforms_count", state.selectedPlatforms.size)
        CrashlyticsHelper.setCustomKey("search_genres_count", state.selectedGenres.size)
        CrashlyticsHelper.setCustomKey("search_has_ordering", ordering.isNotEmpty())
        CrashlyticsHelper.setCustomKey("search_has_rating", rating != null)

        _uiState.update { it.copy(hasSearched = true) }
        _searchParams.value =
            SearchParams(
                query = query,
                platforms = platformIds.ifBlank { null },
                genres = genreIds.ifBlank { null },
                ordering = ordering.ifBlank { null }
            )
        viewModelScope.launch {
            loadGamesUseCase(
                query = _searchParams.value.query,
                platforms = _searchParams.value.platforms,
                genres = _searchParams.value.genres,
                ordering = _searchParams.value.ordering,
                rating = rating
            )
                .cachedIn(viewModelScope)
                .collect { _pagingFlow.value = it }
        }
    }

    fun updateFilters(platforms: List<String>, genres: List<String>, rating: Float) {
        CrashlyticsHelper.setCustomKey("filters_updated", true)
        CrashlyticsHelper.setCustomKey("filters_platforms_count", platforms.size)
        CrashlyticsHelper.setCustomKey("filters_genres_count", genres.size)
        CrashlyticsHelper.setCustomKey("filters_rating", rating.toInt())

        _uiState.update {
            it.copy(selectedPlatforms = platforms, selectedGenres = genres, rating = rating)
        }
    }

    fun updateOrdering(ordering: String) {
        CrashlyticsHelper.setCustomKey("ordering_updated", true)
        CrashlyticsHelper.setCustomKey("ordering_value", ordering)

        _uiState.update { it.copy(ordering = ordering) }
        // KEINE automatische Suche mehr hier!
    }

    fun resetSearch() {
        CrashlyticsHelper.setCustomKey("search_reset", true)
        _uiState.update { it.copy(hasSearched = false) }
        _pagingFlow.value = PagingData.empty()
        currentSearchQuery = ""
    }

    /** Cache verwalten */
    fun clearCache() {
        viewModelScope.launch {
            try {
                CrashlyticsHelper.setCustomKey("cache_clear_attempted", true)
                clearCacheUseCase()
                _cacheSize.value = 0
                CrashlyticsHelper.setCustomKey("cache_clear_success", true)
                AppLogger.d("SearchViewModel", "Cache erfolgreich geleert")
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("cache_clear_error", true)
                CrashlyticsHelper.setCustomKey("cache_clear_exception", e.javaClass.simpleName)
                AppLogger.e("SearchViewModel", "Fehler beim Leeren des Caches")
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
