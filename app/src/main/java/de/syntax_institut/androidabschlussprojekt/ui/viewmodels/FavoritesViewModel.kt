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
import java.io.*

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

    private val _exportResult = MutableStateFlow<Result<Unit>?>(null)
    val exportResult: StateFlow<Result<Unit>?> = _exportResult

    private val _importResult = MutableStateFlow<Result<Unit>?>(null)
    val importResult: StateFlow<Result<Unit>?> = _importResult

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
                }
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Fehler beim Laden der Favoriten", e)
                _uiState.value = FavoritesUiState(error = e.localizedMessage)
            }
        }
    }

    fun clearAllFavorites() {
        AppLogger.d("FavoritesViewModel", "Lösche alle Favoriten")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = clearAllFavoritesUseCase()) {
                is Resource.Success -> {
                    AppLogger.d("FavoritesViewModel", "Alle Favoriten gelöscht")
                    loadFavorites()
                }
                is Resource.Error -> {
                    AppLogger.e("FavoritesViewModel", "Fehler beim Löschen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun removeFavorite(gameId: Int) {
        AppLogger.d("FavoritesViewModel", "Entferne Favorit: $gameId")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = removeFavoriteUseCase(gameId)) {
                is Resource.Success -> {
                    AppLogger.d("FavoritesViewModel", "Favorit entfernt: $gameId")
                    loadFavorites()
                }
                is Resource.Error -> {
                    AppLogger.e("FavoritesViewModel", "Fehler beim Entfernen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun syncFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncFavoritesWithApiUseCase(rawgApi)
                loadFavorites()
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Fehler bei Favoriten-Sync", e)
            }
        }
    }

    fun exportFavorites(context: Context, file: File) =
        viewModelScope.launch(Dispatchers.IO) {
            _exportResult.value = favoritesRepository.exportFavoritesToJson(context, file)
        }

    fun importFavorites(context: Context, file: File) =
        viewModelScope.launch(Dispatchers.IO) {
            _importResult.value = favoritesRepository.importFavoritesFromJson(context, file)
            loadFavorites()
        }

    fun exportFavoritesToUri(context: Context, uri: Uri) =
        viewModelScope.launch(Dispatchers.IO) {
            _exportResult.value = favoritesRepository.exportFavoritesToUri(context, uri)
        }

    fun importFavoritesFromUri(context: Context, uri: Uri) =
        viewModelScope.launch(Dispatchers.IO) {
            _importResult.value = favoritesRepository.importFavoritesFromUri(context, uri)
            loadFavorites()
        }
}
