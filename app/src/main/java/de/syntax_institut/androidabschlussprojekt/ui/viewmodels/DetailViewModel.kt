package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel für Spieldetails.
 */
class DetailViewModel(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getFavoriteByIdUseCase: GetFavoriteByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadDetail(id: Int, forceReload: Boolean = false) {
        AppLogger.d(
            "DetailViewModel",
            "loadDetail() aufgerufen für ID: $id, forceReload=$forceReload"
        )
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DetailUiState(resource = Resource.Loading())

            // forceReload-Logik für Cache ggf. in UseCase auslagern, hier nur noch UseCase-Aufrufe
            val gameResult = getGameDetailUseCase(id)
            val favoriteResult = isFavoriteUseCase(id)

            when (gameResult) {
                is Resource.Success -> {
                    var game = gameResult.data

                    // Wenn es ein Favorit ist und forceReload aktiviert ist, hole die vollständigen Daten aus dem Favoriten
                    if (forceReload && favoriteResult) {
                        val favoriteGame = getFavoriteByIdUseCase(id)
                        if (favoriteGame != null) {
                            game = favoriteGame
                        }
                    }

                    if ((game?.website.isNullOrBlank() && game?.screenshots.isNullOrEmpty()) && !forceReload) {
                        loadDetail(id, forceReload = true)
                    } else if (game != null) {
                        _uiState.value =
                            DetailUiState(resource = Resource.Success(game), game = game)
                        _isFavorite.value = favoriteResult
                    } else {
                        _uiState.value = DetailUiState(
                            resource = Resource.Error("Spiel konnte nicht geladen werden."),
                            error = "Spiel konnte nicht geladen werden."
                        )
                    }
                }

                is Resource.Error -> {
                    AppLogger.e("DetailViewModel", "Fehler beim Laden: ${gameResult.message}")
                    _uiState.value = DetailUiState(
                        resource = Resource.Error(gameResult.message ?: "Unbekannter Fehler"),
                        error = gameResult.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = DetailUiState(resource = Resource.Loading())
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentGame = _uiState.value.game
        if (currentGame != null) {
            AppLogger.d(
                "DetailViewModel",
                "toggleFavorite() aufgerufen für Spiel: ${currentGame.title}"
            )
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = toggleFavoriteUseCase(currentGame)) {
                    is Resource.Success -> {
                        _isFavorite.value = result.data == true
                        if (result.data == true) {
                            // Hole die vollständigen Daten aus dem Favoriten
                            val favoriteGame = getFavoriteByIdUseCase(currentGame.id)
                            if (favoriteGame != null) {
                                _uiState.value = _uiState.value.copy(
                                    resource = Resource.Success(favoriteGame),
                                    game = favoriteGame
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    resource = Resource.Success(currentGame),
                                    game = currentGame
                                )
                            }
                        } else {
                            // Spiel wurde aus Favoriten entfernt - verwende die aktuellen Daten
                            _uiState.value = _uiState.value.copy(
                                resource = Resource.Success(currentGame),
                                game = currentGame
                            )
                        }
                    }
                    is Resource.Error -> {
                        AppLogger.e(
                            "DetailViewModel",
                            "Fehler beim Umschalten des Favoriten: ${result.message}"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun updateUserRating(rating: Float) {
        _uiState.value = _uiState.value.copy(userRating = rating)
        AppLogger.d("DetailViewModel", "User Rating aktualisiert: $rating")
    }
}

