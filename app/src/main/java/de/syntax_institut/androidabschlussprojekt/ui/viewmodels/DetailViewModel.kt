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
            _uiState.value = DetailUiState(resource = Resource.Loading())

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

                    // Wenn es ein Favorit ist und forceReload aktiviert ist, hole die vollständigen Daten aus dem Favoriten
                    if (forceReload && favoriteResult) {
                        Log.d(
                            "DetailViewModel",
                            "[DEBUG] forceReload für Favorit - hole vollständige Daten"
                        )
                        val favoriteGame = favoritesRepo.getFavoriteById(id)
                        if (favoriteGame != null) {
                            Log.d(
                                "DetailViewModel",
                                "[DEBUG] Favorit gefunden mit ${favoriteGame.screenshots.size} Screenshots"
                            )
                            // Verwende die vollständigen Daten aus dem Favoriten
                            game = favoriteGame
                        }
                    }

                    // Fallback: Verwende gecachte Screenshots wenn verfügbar
                    if (forceReload && cachedGame != null && cachedGame.screenshots.isNotEmpty() &&
                        (game?.screenshots.isNullOrEmpty() == true)
                    ) {
                        Log.d(
                            "DetailViewModel",
                            "[DEBUG] forceReload: Übernehme gecachte Screenshots (${cachedGame.screenshots.size})"
                        )
                        game = game?.copy(screenshots = cachedGame.screenshots)
                    }

                    Log.d(
                        "DetailViewModel",
                        "Final geladen: Website='${game?.website}', Screenshots=${game?.screenshots?.size ?: 0}, Movies=${game?.movies?.size ?: 0}"
                    )

                    if ((game?.website.isNullOrBlank() == true && game?.screenshots.isNullOrEmpty() == true) && !forceReload) {
                        Log.d(
                            "DetailViewModel",
                            "[DEBUG] Website und Screenshots leer, versuche forceReload für $id"
                        )
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
                    Log.e("DetailViewModel", "[ERROR] Fehler beim Laden: ${gameResult.message}")
                    _uiState.value = DetailUiState(
                        resource = Resource.Error(gameResult.message ?: "Fehler"),
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
            Log.d(
                "DetailViewModel",
                "[DEBUG] toggleFavorite() aufgerufen für Spiel: ${currentGame.title}"
            )
            Log.d(
                "DetailViewModel",
                "[DEBUG] Aktuelle Screenshots: ${currentGame.screenshots.size}"
            )
            Log.d("DetailViewModel", "[DEBUG] Aktuelle Movies: ${currentGame.movies.size}")
            
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = favoritesRepo.toggleFavorite(currentGame)) {
                    is Resource.Success -> {
                        _isFavorite.value = result.data == true
                        Log.d("DetailViewModel", "Favorit umgeschaltet: ${result.data}")

                        // Wenn das Spiel jetzt ein Favorit ist, stelle sicher dass die vollständigen Daten erhalten bleiben
                        if (result.data == true) {
                            // Hole die vollständigen Daten aus dem Favoriten
                            val favoriteGame = favoritesRepo.getFavoriteById(currentGame.id)
                            if (favoriteGame != null) {
                                Log.d(
                                    "DetailViewModel",
                                    "[DEBUG] Nach toggleFavorite - Vollständige Daten geladen: Screenshots=${favoriteGame.screenshots.size}, Movies=${favoriteGame.movies.size}"
                                )

                                // Aktualisiere den UI-State mit den vollständigen Daten
                                _uiState.value = _uiState.value.copy(
                                    resource = Resource.Success(favoriteGame),
                                    game = favoriteGame
                                )
                            } else {
                                // Fallback: Verwende die aktuellen Daten
                                Log.d(
                                    "DetailViewModel",
                                    "[DEBUG] Nach toggleFavorite - Verwende aktuelle Daten: Screenshots=${currentGame.screenshots.size}, Movies=${currentGame.movies.size}"
                                )
                                _uiState.value = _uiState.value.copy(
                                    resource = Resource.Success(currentGame),
                                    game = currentGame
                                )
                            }
                        } else {
                            // Spiel wurde aus Favoriten entfernt - verwende die aktuellen Daten
                            Log.d(
                                "DetailViewModel",
                                "[DEBUG] Nach toggleFavorite - Spiel aus Favoriten entfernt: Screenshots=${currentGame.screenshots.size}, Movies=${currentGame.movies.size}"
                            )
                            _uiState.value = _uiState.value.copy(
                                resource = Resource.Success(currentGame),
                                game = currentGame
                            )
                        }
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

