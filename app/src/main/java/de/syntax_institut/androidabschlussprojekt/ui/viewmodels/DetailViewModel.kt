package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.app.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** ViewModel für Spieldetails. */
class DetailViewModel(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleWishlistGameUseCase: ToggleWishlistGameUseCase,
    private val isInWishlistUseCase: IsInWishlistUseCase,
    savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application) {

    private val gameId: Int = checkNotNull(savedStateHandle["gameId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        // Setze Custom Keys für Crashlytics-Tracking
        CrashlyticsHelper.setCustomKey("detail_screen_game_id", gameId)
        CrashlyticsHelper.setCustomKey("current_screen", "DetailScreen")

        loadGameDetail()
    }

    private fun loadGameDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                CrashlyticsHelper.setCustomKey("detail_loading_started", true)

                val result = getGameDetailUseCase(gameId)
                when (result) {
                    is Resource.Success -> {
                        val game = result.data
                        val isFavorite = isFavoriteUseCase(gameId)
                        val isInWishlist = isInWishlistUseCase(gameId)

                        CrashlyticsHelper.setCustomKey("detail_loading_success", true)
                        CrashlyticsHelper.setCustomKey("game_title", game?.title ?: "")
                        CrashlyticsHelper.setCustomKey("game_rating", game?.rating?.toInt() ?: 0)
                        CrashlyticsHelper.setCustomKey(
                            "screenshots_count",
                            game?.screenshots?.size ?: 0
                        )
                        CrashlyticsHelper.setCustomKey("movies_count", game?.movies?.size ?: 0)

                        _uiState.value =
                            _uiState.value.copy(
                                game = game,
                                isLoading = false,
                                isFavorite = isFavorite,
                                isInWishlist = isInWishlist
                            )
                    }
                    is Resource.Error -> {
                        CrashlyticsHelper.setCustomKey("detail_loading_error", true)
                        CrashlyticsHelper.setCustomKey("detail_error_message", result.message ?: "")

                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = result.message)
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("detail_loading_exception", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("detail_exception_message", e.message ?: "Unknown")

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            "Ein unerwarteter Fehler ist aufgetreten: ${e.localizedMessage}"
                    )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                CrashlyticsHelper.setCustomKey("favorite_toggle_attempted", true)
                CrashlyticsHelper.setCustomKey("favorite_toggle_game_id", gameId)

                val game = _uiState.value.game
                if (game != null) {
                    val result =
                        toggleFavoriteUseCase(
                            getApplication<Application>().applicationContext,
                            game
                        )
                    if (result is Resource.Success) {
                        val newFavoriteState = result.data ?: false
                        CrashlyticsHelper.setCustomKey("favorite_toggle_success", true)
                        CrashlyticsHelper.setCustomKey("favorite_new_state", newFavoriteState)
                        _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)
                    } else {
                        CrashlyticsHelper.setCustomKey("favorite_toggle_error", true)
                        CrashlyticsHelper.setCustomKey(
                            "favorite_error_message",
                            result.message ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("favorite_toggle_exception", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("favorite_exception_message", e.message ?: "Unknown")
            }
        }
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            try {
                CrashlyticsHelper.setCustomKey("wishlist_toggle_attempted", true)
                CrashlyticsHelper.setCustomKey("wishlist_toggle_game_id", gameId)

                val game = _uiState.value.game
                if (game != null) {
                    val result =
                        toggleWishlistGameUseCase(
                            getApplication<Application>().applicationContext,
                            game
                        )
                    if (result is Resource.Success) {
                        val newWishlistState = result.data ?: false
                        CrashlyticsHelper.setCustomKey("wishlist_toggle_success", true)
                        CrashlyticsHelper.setCustomKey("wishlist_new_state", newWishlistState)
                        _uiState.value = _uiState.value.copy(isInWishlist = newWishlistState)
                    } else {
                        CrashlyticsHelper.setCustomKey("wishlist_toggle_error", true)
                        CrashlyticsHelper.setCustomKey(
                            "wishlist_error_message",
                            result.message ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("wishlist_toggle_exception", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("wishlist_exception_message", e.message ?: "Unknown")
            }
        }
    }

    fun refresh() {
        CrashlyticsHelper.setCustomKey("detail_refresh_attempted", true)
        loadGameDetail()
    }

    fun updateUserRating(rating: Float) {
        _uiState.value = _uiState.value.copy(userRating = rating)
        AppLogger.d("DetailViewModel", "User Rating aktualisiert: $rating")
    }
}
