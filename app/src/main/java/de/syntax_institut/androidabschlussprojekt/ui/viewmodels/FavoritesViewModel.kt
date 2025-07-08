package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel für die Favoriten-Liste.
 */
class FavoritesViewModel(
    private val favoritesRepo: FavoritesRepository,
    private val rawgApi: RawgApi,
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
                favoritesRepo.getAllFavorites().collect { favorites ->
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
            when (val result = favoritesRepo.clearAllFavorites()) {
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
            when (val result = favoritesRepo.removeFavorite(gameId)) {
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
                favoritesRepo.syncFavoritesWithApi(rawgApi)
                loadFavorites()
            } catch (e: Exception) {
                AppLogger.e("FavoritesViewModel", "Fehler bei Favoriten-Sync", e)
            }
        }
    }
} 