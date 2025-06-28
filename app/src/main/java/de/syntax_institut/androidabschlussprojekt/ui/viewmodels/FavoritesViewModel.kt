package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.repositories.FavoritesRepository
import de.syntax_institut.androidabschlussprojekt.ui.states.FavoritesUiState
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel für die Favoriten-Liste.
 */
class FavoritesViewModel(
    private val favoritesRepo: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        Log.d("FavoritesViewModel", "Lade Favoriten")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = FavoritesUiState(isLoading = true)
            
            try {
                favoritesRepo.getAllFavorites().collect { favorites ->
                    Log.d("FavoritesViewModel", "Favoriten geladen: ${favorites.size}")
                    _uiState.value = FavoritesUiState(favorites = favorites)
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Fehler beim Laden der Favoriten", e)
                _uiState.value = FavoritesUiState(error = e.localizedMessage)
            }
        }
    }

    fun clearAllFavorites() {
        Log.d("FavoritesViewModel", "Lösche alle Favoriten")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = favoritesRepo.clearAllFavorites()) {
                is Resource.Success -> {
                    Log.d("FavoritesViewModel", "Alle Favoriten gelöscht")
                    loadFavorites() // Reload to update UI
                }
                is Resource.Error -> {
                    Log.e("FavoritesViewModel", "Fehler beim Löschen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun removeFavorite(gameId: Int) {
        Log.d("FavoritesViewModel", "Entferne Favorit: $gameId")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = favoritesRepo.removeFavorite(gameId)) {
                is Resource.Success -> {
                    Log.d("FavoritesViewModel", "Favorit entfernt: $gameId")
                    loadFavorites() // Reload to update UI
                }
                is Resource.Error -> {
                    Log.e("FavoritesViewModel", "Fehler beim Entfernen: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }
} 