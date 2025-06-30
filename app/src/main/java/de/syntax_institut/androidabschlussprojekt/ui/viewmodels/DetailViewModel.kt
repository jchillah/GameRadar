package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.util.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
                    val game = gameResult.data
                    Log.d("DetailViewModel", "Erfolgreich geladen: ${game?.title}")
                    Log.d("DetailViewModel", "Screenshots: ${game?.screenshots?.size ?: 0}")
                    game?.screenshots?.forEachIndexed { index, url ->
                        Log.d("DetailViewModel", "Screenshot $index: $url")
                    }
                    _uiState.value = DetailUiState(game = game)
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
                        _isFavorite.value = result.data == true
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

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.clearCache()
            Log.d("DetailViewModel", "Cache gelöscht")
        }
    }

    fun updateUserRating(rating: Float) {
        _uiState.value = _uiState.value.copy(userRating = rating)
        Log.d("DetailViewModel", "User Rating aktualisiert: $rating")
    }
}

