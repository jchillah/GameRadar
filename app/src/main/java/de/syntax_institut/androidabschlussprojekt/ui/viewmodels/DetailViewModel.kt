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

    fun loadDetail(id: Int, forceReload: Boolean = false) {
        Log.d(
            "DetailViewModel",
            "[DEBUG] loadDetail() aufgerufen für ID: $id, forceReload=$forceReload"
        )
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DetailUiState(isLoading = true)

            if (forceReload) {
                repo.clearCache()
                Log.d("DetailViewModel", "Cache für Detailansicht gelöscht")
            }

            val cachedGame = repo.getGameFromCache(id)
            Log.d(
                "DetailViewModel",
                "[DEBUG] cachedGame: ${cachedGame != null}, Screenshots: ${cachedGame?.screenshots?.size ?: 0}"
            )
            val gameResult = repo.getGameDetail(id)
            Log.d(
                "DetailViewModel",
                "[DEBUG] gameResult: ${gameResult is Resource.Success}, error: ${(gameResult as? Resource.Error)?.message}"
            )
            val favoriteResult = favoritesRepo.isFavorite(id)

            when (gameResult) {
                is Resource.Success -> {
                    var game = gameResult.data
                    Log.d(
                        "DetailViewModel",
                        "[DEBUG] Resource.Success: Website='${game?.website}', Screenshots=${game?.screenshots?.size ?: 0}"
                    )
                    if (forceReload && cachedGame != null && cachedGame.screenshots.isNotEmpty()) {
                        Log.d(
                            "DetailViewModel",
                            "[DEBUG] forceReload: Übernehme gecachte Screenshots (${cachedGame.screenshots.size})"
                        )
                        game = game?.copy(screenshots = cachedGame.screenshots)
                    }
                    Log.d(
                        "DetailViewModel",
                        "Geladen: Website='${game?.website}', Screenshots=${game?.screenshots?.size ?: 0}"
                    )

                    if ((game?.website.isNullOrBlank() == true && game?.screenshots.isNullOrEmpty() == true) && !forceReload) {
                        Log.d(
                            "DetailViewModel",
                            "[DEBUG] Website und Screenshots leer, versuche forceReload für $id"
                        )
                        loadDetail(id, forceReload = true)
                    } else {
                        _uiState.value = DetailUiState(game = game)
                        _isFavorite.value = favoriteResult
                    }
                }

                is Resource.Error -> {
                    Log.e("DetailViewModel", "[ERROR] Fehler beim Laden: ${gameResult.message}")
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

    fun updateUserRating(rating: Float) {
        _uiState.value = _uiState.value.copy(userRating = rating)
        Log.d("DetailViewModel", "User Rating aktualisiert: $rating")
    }
}

