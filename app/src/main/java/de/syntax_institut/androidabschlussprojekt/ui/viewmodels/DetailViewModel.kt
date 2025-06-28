package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.data.repositories.FavoritesRepository
import de.syntax_institut.androidabschlussprojekt.ui.states.DetailUiState
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel für Spieldetails.
 */
class DetailViewModel(
    private val repo: GameRepository,
    private val favoritesRepo: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadDetail(id: Int) {
        Log.d("DetailViewModel", "Lade Spieldetails für ID: $id")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DetailUiState(isLoading = true)
            
            // Lade Spieldetails und Favoriten-Status parallel
            val gameResult = repo.getGameDetail(id)
            val favoriteResult = favoritesRepo.isFavorite(id)
            
            when (gameResult) {
                is Resource.Success -> {
                    Log.d("DetailViewModel", "Erfolgreich geladen: ${gameResult.data}")
                    _uiState.value = DetailUiState(game = gameResult.data)
                    _isFavorite.value = favoriteResult
                }
                is Resource.Error -> {
                    Log.e("DetailViewModel", "Fehler beim Laden: ${gameResult.message}")
                    _uiState.value = DetailUiState(error = gameResult.message)
                }
                else -> {}
            }
        }
    }

    fun toggleFavorite() {
        val currentGame = _uiState.value.game
        if (currentGame != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = favoritesRepo.toggleFavorite(currentGame)) {
                    is Resource.Success -> {
                        _isFavorite.value = result.data ?: false
                        Log.d("DetailViewModel", "Favorit umgeschaltet: ${result.data}")
                    }
                    is Resource.Error -> {
                        Log.e("DetailViewModel", "Fehler beim Umschalten des Favoriten: ${result.message}")
                        // Hier könnte man einen Snackbar oder Toast anzeigen
                    }
                    else -> {}
                }
            }
        }
    }
}

