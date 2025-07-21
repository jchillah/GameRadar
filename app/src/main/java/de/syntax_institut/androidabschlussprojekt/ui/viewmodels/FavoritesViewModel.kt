package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.content.*
import android.net.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** ViewModel für die Favoriten-Liste. */
class FavoritesViewModel(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val clearAllFavoritesUseCase: ClearAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val syncFavoritesWithApiUseCase: SyncFavoritesWithApiUseCase,
    private val rawgApi: RawgApi,
    private val favoritesRepository:
    de.syntax_institut.androidabschlussprojekt.data.repositories.FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
        syncFavorites()
        viewModelScope.launch {
            while (true) {
                delay(12 * 60 * 60 * 1000L)
                syncFavorites()
            }
        }
    }

    fun loadFavorites() {
        AppLogger.d("FavoritesViewModel", "Lade Favoriten")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = FavoritesUiState(isLoading = true)
            try {
                getAllFavoritesUseCase().collect { favorites ->
                    AppLogger.d("FavoritesViewModel", "Favoriten geladen: ${favorites.size}")
                    val sortedFavorites = favorites.sortedBy { it.title.lowercase() }
                    _uiState.value = FavoritesUiState(favorites = sortedFavorites)

                    // Analytics-Tracking für geladene Favoriten
                    AppAnalytics.trackUserAction("favorites_loaded")
                    AppAnalytics.trackPerformanceMetric("favorites_count", favorites.size, "count")
                }
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Fehler beim Laden der Favoriten", e)
                _uiState.value = FavoritesUiState(error = e.localizedMessage)

                // Error-Tracking
                CrashlyticsHelper.recordFavoriteError(
                    "load_favorites",
                    0,
                    e.message ?: "Unknown error"
                )
                AppAnalytics.trackError(
                    "Failed to load favorites: ${e.message}",
                    "FavoritesViewModel"
                )
            }
        }
    }

    fun clearAllFavorites(context: Context) {
        AppLogger.d("FavoritesViewModel", "Lösche alle Favoriten")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = clearAllFavoritesUseCase(context)) {
                is Resource.Success -> {
                    AppLogger.d("FavoritesViewModel", "Alle Favoriten gelöscht")
                    // Analytics-Tracking
                    AppAnalytics.trackUserAction("favorites_cleared_all")
                    AppAnalytics.trackCacheOperation(
                        "clear_favorites",
                        _uiState.value.favorites.size,
                        true
                    )
                    loadFavorites()
                }
                is Resource.Error -> {
                    AppLogger.e("FavoritesViewModel", "Fehler beim Löschen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                    // Error-Tracking
                    CrashlyticsHelper.recordFavoriteError(
                        "clear_all",
                        0,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to clear favorites: ${result.message}",
                        "FavoritesViewModel"
                    )
                }
                else -> {}
            }
        }
    }

    fun removeFavorite(context: Context, gameId: Int) {
        AppLogger.d("FavoritesViewModel", "Entferne Favorit: $gameId")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = removeFavoriteUseCase(context, gameId)) {
                is Resource.Success -> {
                    AppLogger.d("FavoritesViewModel", "Favorit entfernt: $gameId")
                    // Analytics-Tracking
                    AppAnalytics.trackGameInteraction(gameId.toString(), "remove_from_favorites")
                    AppAnalytics.trackUserAction("favorite_removed", gameId)
                    loadFavorites()
                }
                is Resource.Error -> {
                    AppLogger.e("FavoritesViewModel", "Fehler beim Entfernen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                    // Error-Tracking
                    CrashlyticsHelper.recordFavoriteError(
                        "remove_favorite",
                        gameId,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to remove favorite: ${result.message}",
                        "FavoritesViewModel"
                    )
                }
                else -> {}
            }
        }
    }

    fun syncFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncFavoritesWithApiUseCase(rawgApi)
                // Analytics-Tracking
                AppAnalytics.trackUserAction("favorites_synced")
                AppAnalytics.trackCacheOperation(
                    "sync_favorites",
                    _uiState.value.favorites.size,
                    true
                )
                loadFavorites()
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Fehler bei Favoriten-Sync", e)
                // Error-Tracking
                CrashlyticsHelper.recordFavoriteError(
                    "sync_favorites",
                    0,
                    e.message ?: "Unknown error"
                )
                AppAnalytics.trackError(
                    "Failed to sync favorites: ${e.message}",
                    "FavoritesViewModel"
                )
            }
        }
    }

    fun exportFavoritesToUri(context: Context, uri: Uri) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = favoritesRepository.exportFavoritesToUri(context, uri)

                // Analytics-Tracking
                if (result.isSuccess) {
                    AppAnalytics.trackCacheOperation(
                        "export_favorites",
                        _uiState.value.favorites.size,
                        true
                    )
                    AppAnalytics.trackUserAction("favorites_exported")

                    // Export-Ergebnis für UI verwenden
                    _uiState.value = _uiState.value.copy(
                        exportSuccess = true,
                        exportMessage = "Favoriten erfolgreich exportiert"
                    )
                } else {
                    val exception = result.exceptionOrNull()
                    AppAnalytics.trackCacheOperation(
                        "export_favorites",
                        _uiState.value.favorites.size,
                        false
                    )
                    AppAnalytics.trackError(
                        "Failed to export favorites: ${exception?.message}",
                        "FavoritesViewModel"
                    )

                    // Export-Fehler für UI verwenden
                    _uiState.value = _uiState.value.copy(
                        exportSuccess = false,
                        exportMessage = exception?.message ?: "Export fehlgeschlagen"
                    )

                    // Crashlytics Error Recording
                    CrashlyticsHelper.recordFavoriteError(
                        "export_favorites",
                        0,
                        exception?.message ?: "Unknown error"
                    )
                }
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Exception beim Export", e)

                // Error-Tracking
                CrashlyticsHelper.recordFavoriteError(
                    "export_favorites",
                    0,
                    e.message ?: "Unknown error"
                )
                AppAnalytics.trackError("Export exception: ${e.message}", "FavoritesViewModel")

                // Export-Fehler für UI verwenden
                _uiState.value = _uiState.value.copy(
                    exportSuccess = false,
                    exportMessage = e.message ?: "Export fehlgeschlagen"
                )
            }
        }

    fun importFavoritesFromUri(context: Context, uri: Uri) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = favoritesRepository.importFavoritesFromUri(context, uri)

                // Analytics-Tracking
                if (result.isSuccess) {
                    AppAnalytics.trackCacheOperation(
                        "import_favorites",
                        _uiState.value.favorites.size,
                        true
                    )
                    AppAnalytics.trackUserAction("favorites_imported")

                    // Import-Ergebnis für UI verwenden
                    _uiState.value = _uiState.value.copy(
                        importSuccess = true,
                        importMessage = "Favoriten erfolgreich importiert"
                    )
                } else {
                    val exception = result.exceptionOrNull()
                    AppAnalytics.trackCacheOperation(
                        "import_favorites",
                        _uiState.value.favorites.size,
                        false
                    )
                    AppAnalytics.trackError(
                        "Failed to import favorites: ${exception?.message}",
                        "FavoritesViewModel"
                    )

                    // Import-Fehler für UI verwenden
                    _uiState.value = _uiState.value.copy(
                        importSuccess = false,
                        importMessage = exception?.message ?: "Import fehlgeschlagen"
                    )

                    // Crashlytics Error Recording
                    CrashlyticsHelper.recordFavoriteError(
                        "import_favorites",
                        0,
                        exception?.message ?: "Unknown error"
                    )
                }

                // Favoriten neu laden nach Import
                loadFavorites()
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Exception beim Import", e)

                // Error-Tracking
                CrashlyticsHelper.recordFavoriteError(
                    "import_favorites",
                    0,
                    e.message ?: "Unknown error"
                )
                AppAnalytics.trackError("Import exception: ${e.message}", "FavoritesViewModel")

                // Import-Fehler für UI verwenden
                _uiState.value = _uiState.value.copy(
                    importSuccess = false,
                    importMessage = e.message ?: "Import fehlgeschlagen"
                )
            }
        }

    /**
     * Setzt Export-Ergebnis zurück.
     */
    fun clearExportResult() {
        _uiState.value = _uiState.value.copy(
            exportSuccess = null,
            exportMessage = null
        )
    }

    /**
     * Setzt Import-Ergebnis zurück.
     */
    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(
            importSuccess = null,
            importMessage = null
        )
    }
}
