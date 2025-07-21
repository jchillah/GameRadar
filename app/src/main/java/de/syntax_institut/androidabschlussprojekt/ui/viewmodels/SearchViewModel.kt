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

/**
 * ViewModel für die Suchfunktionalität mit erweiterten Filtern und Paging.
 *
 * Features:
 * - Paging-basierte Spielesuche mit RAWG API
 * - Erweiterte Filter: Plattformen, Genres, Bewertung, Sortierung
 * - Cache-Management und Monitoring
 * - Offline-Unterstützung
 * - Analytics-Tracking und Crashlytics-Integration
 * - Automatische Cache-Größenüberwachung
 * - Performance-Monitoring für alle Operationen
 * - Robuste Fehlerbehandlung mit Crashlytics
 *
 * @param loadGamesUseCase UseCase für das Laden von Spielen
 * @param getPlatformsUseCase UseCase für das Abrufen von Plattformen
 * @param getGenresUseCase UseCase für das Abrufen von Genres
 * @param getCacheSizeUseCase UseCase für Cache-Größenabfrage
 * @param clearCacheUseCase UseCase für Cache-Löschung
 */
class SearchViewModel(
    private val loadGamesUseCase: LoadGamesUseCase,
    private val getPlatformsUseCase: GetPlatformsUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val getCacheSizeUseCase: GetCacheSizeUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

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

        // Logging für State-Management
        AppLogger.d("SearchViewModel", "SearchViewModel initialisiert")
    }

    /**
     * Initialisiert das Cache-Monitoring mit periodischen Updates. Überwacht die Cache-Größe und
     * aktualisiert den UI-State entsprechend.
     */
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

    /**
     * Lädt alle verfügbaren Plattformen von der API. Aktualisiert den UI-State mit Loading-Status
     * und Fehlerbehandlung.
     */
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

                    // Crashlytics Error Recording
                    CrashlyticsHelper.recordApiError(
                        "platforms",
                        0,
                        platformResponse.message ?: "Unknown error"
                    )
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

                // Crashlytics Error Recording
                CrashlyticsHelper.recordException(e)
            }
        }
    }

    /**
     * Lädt alle verfügbaren Genres von der API. Aktualisiert den UI-State mit Loading-Status und
     * Fehlerbehandlung.
     */
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

                    // Crashlytics Error Recording
                    CrashlyticsHelper.recordApiError(
                        "genres",
                        0,
                        genreResponse.message ?: "Unknown error"
                    )
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("genres_loading_exception", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("genres_exception_message", e.message ?: "Unknown")

                val errorId = de.syntax_institut.androidabschlussprojekt.R.string.error_load_genres
                _uiState.update { it.copy(genresErrorId = errorId, isLoadingGenres = false) }

                // Crashlytics Error Recording
                CrashlyticsHelper.recordException(e)
            }
        }
    }

    /**
     * Führt eine Suche mit den aktuellen Filtern durch. Verwendet Paging für große Ergebnislisten
     * und aktualisiert den UI-State.
     *
     * @param query Der Suchbegriff
     */
    fun search(query: String) {
        CrashlyticsHelper.setCustomKey("search_attempted", true)
        CrashlyticsHelper.setCustomKey("search_query", query)
        CrashlyticsHelper.setCustomKey("search_query_length", query.length)

        // Analytics-Tracking
        AppAnalytics.trackUserAction("search", query.length)
        AppAnalytics.trackPerformanceMetric("search_query_length", query.length, "characters")

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

    /**
     * Aktualisiert die Filter-Einstellungen.
     *
     * @param platforms Liste der ausgewählten Plattform-IDs
     * @param genres Liste der ausgewählten Genre-IDs
     * @param rating Mindestbewertung (0-5)
     */
    fun updateFilters(platforms: List<String>, genres: List<String>, rating: Float) {
        CrashlyticsHelper.setCustomKey("filters_updated", true)
        CrashlyticsHelper.setCustomKey("filters_platforms_count", platforms.size)
        CrashlyticsHelper.setCustomKey("filters_genres_count", genres.size)
        CrashlyticsHelper.setCustomKey("filters_rating", rating.toInt())

        _uiState.update {
            it.copy(selectedPlatforms = platforms, selectedGenres = genres, rating = rating)
        }
    }

    /**
     * Aktualisiert die Sortierreihenfolge.
     *
     * @param ordering Die Sortierreihenfolge (z.B. "-rating", "name", etc.)
     */
    fun updateOrdering(ordering: String) {
        CrashlyticsHelper.setCustomKey("ordering_updated", true)
        CrashlyticsHelper.setCustomKey("ordering_value", ordering)

        _uiState.update { it.copy(ordering = ordering) }
        // KEINE automatische Suche mehr hier!
    }

    /** Setzt die Suche zurück und leert die Ergebnisse. */
    fun resetSearch() {
        CrashlyticsHelper.setCustomKey("search_reset", true)
        _uiState.update { it.copy(hasSearched = false) }
        _pagingFlow.value = PagingData.empty()
        currentSearchQuery = ""
    }

    /**
     * Leert den gesamten Cache und aktualisiert die Cache-Statistiken. Behandelt Fehler mit
     * Crashlytics-Integration.
     */
    fun clearCache() {
        viewModelScope.launch {
            try {
                CrashlyticsHelper.setCustomKey("cache_clear_attempted", true)
                clearCacheUseCase()
                _cacheSize.value = 0
                CrashlyticsHelper.setCustomKey("cache_clear_success", true)

                // Analytics-Tracking
                AppAnalytics.trackCacheOperation("clear_cache", _cacheSize.value, true)
                AppAnalytics.trackUserAction("cache_cleared")

                AppLogger.d("SearchViewModel", "Cache erfolgreich geleert")
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("cache_clear_error", true)
                CrashlyticsHelper.setCustomKey("cache_clear_exception", e.javaClass.simpleName)
                AppLogger.e("SearchViewModel", "Fehler beim Leeren des Caches")

                // Analytics-Tracking für Fehler
                AppAnalytics.trackCacheOperation("clear_cache", _cacheSize.value, false)
                AppAnalytics.trackError("Cache clear failed: ${e.message}", "SearchViewModel")

                // Crashlytics Error Recording
                CrashlyticsHelper.recordCacheError(
                    "clear_cache",
                    _cacheSize.value,
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Entfernt einen Plattform-Filter und führt die Suche erneut aus.
     *
     * @param platformId Die ID der zu entfernenden Plattform
     */
    fun removePlatformFilter(platformId: String) {
        val newPlatforms = _uiState.value.selectedPlatforms.filterNot { it == platformId }
        _uiState.update { it.copy(selectedPlatforms = newPlatforms) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    /**
     * Entfernt einen Genre-Filter und führt die Suche erneut aus.
     *
     * @param genreId Die ID des zu entfernenden Genres
     */
    fun removeGenreFilter(genreId: String) {
        val newGenres = _uiState.value.selectedGenres.filterNot { it == genreId }
        _uiState.update { it.copy(selectedGenres = newGenres) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    /** Entfernt den Bewertungsfilter und führt die Suche erneut aus. */
    fun removeRatingFilter() {
        _uiState.update { it.copy(rating = 0f) }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    /** Entfernt den Sortierungsfilter und führt die Suche erneut aus. */
    fun removeOrderingFilter() {
        _uiState.update { it.copy(ordering = "") }
        if (currentSearchQuery.isNotBlank()) search(currentSearchQuery)
    }

    /** Entfernt alle Filter und führt die Suche erneut aus. */
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
