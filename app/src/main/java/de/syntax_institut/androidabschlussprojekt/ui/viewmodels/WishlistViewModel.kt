package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.app.*
import android.content.*
import android.net.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel für die Wunschliste. Kapselt alle UI-States, Datenoperationen und Export/Import-Logik
 * für die Wishlist.
 *
 * Wird von [WishlistScreen] verwendet und stellt alle Funktionen für das Hinzufügen, Entfernen,
 * Exportieren und Importieren von Wunschlistenspielen bereit.
 */
class WishlistViewModel(
    private val addWishlistGameUseCase: AddWishlistGameUseCase,
    private val removeWishlistGameUseCase: RemoveWishlistGameUseCase,
    private val toggleWishlistGameUseCase: ToggleWishlistGameUseCase,
    private val getAllWishlistGamesUseCase: GetAllWishlistGamesUseCase,
    private val clearAllWishlistGamesUseCase: ClearAllWishlistGamesUseCase,
    private val isInWishlistUseCase: IsInWishlistUseCase,
    private val getWishlistGameByIdUseCase: GetWishlistGameByIdUseCase,
    private val getWishlistCountUseCase: GetWishlistCountUseCase,
    private val searchWishlistGamesUseCase: SearchWishlistGamesUseCase,
    private val exportWishlistToUriUseCase: ExportWishlistToUriUseCase,
    private val importWishlistFromUriUseCase: ImportWishlistFromUriUseCase,
    application: Application,
) : AndroidViewModel(application) {

    private val _wishlistGames = MutableStateFlow<List<Game>>(emptyList())
    val wishlistGames: StateFlow<List<Game>> = _wishlistGames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _exportResult = MutableStateFlow<Resource<Unit>?>(null)
    val exportResult: StateFlow<Resource<Unit>?> = _exportResult.asStateFlow()

    private val _importResult = MutableStateFlow<Resource<Unit>?>(null)
    val importResult: StateFlow<Resource<Unit>?> = _importResult.asStateFlow()

    // --- NEU: State für das aktuell angezeigte Detailspiel ---
    private val _detailGame = MutableStateFlow<Game?>(null)
    val detailGame: StateFlow<Game?> = _detailGame.asStateFlow()

    // Methode zum Setzen des Detailspiels (z.B. nach getWishlistGameById)
    fun setDetailGame(game: Game?) {
        _detailGame.value = game
    }

    // Methode zum Zurücksetzen des Detailspiels (z.B. beim Tab-Wechsel)
    fun clearDetailGame() {
        _detailGame.value = null
    }

    init {
        loadWishlist()
    }

    fun loadWishlist() {
        viewModelScope.launch {
            getAllWishlistGamesUseCase().collect { games -> _wishlistGames.value = games }
        }
    }

    fun addToWishlist(game: Game) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result =
                addWishlistGameUseCase(
                    getApplication<Application>().applicationContext,
                    game
                )
            ) {
                is Resource.Success -> {
                    _error.value = null
                    // Analytics-Tracking
                    AppAnalytics.trackGameInteraction(game.id.toString(), "add_to_wishlist")
                    AppAnalytics.trackUserAction("wishlist_added", game.id)
                }

                is Resource.Error -> {
                    _error.value = result.message
                    // Error-Tracking
                    CrashlyticsHelper.recordWishlistError(
                        "add_to_wishlist",
                        game.id,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to add to wishlist: ${result.message}",
                        "WishlistViewModel"
                    )
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun removeFromWishlist(gameId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result =
                removeWishlistGameUseCase(
                    getApplication<Application>().applicationContext,
                    gameId
                )
            ) {
                is Resource.Success -> {
                    _error.value = null
                    // Analytics-Tracking
                    AppAnalytics.trackGameInteraction(gameId.toString(), "remove_from_wishlist")
                    AppAnalytics.trackUserAction("wishlist_removed", gameId)
                }

                is Resource.Error -> {
                    _error.value = result.message
                    // Error-Tracking
                    CrashlyticsHelper.recordWishlistError(
                        "remove_from_wishlist",
                        gameId,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to remove from wishlist: ${result.message}",
                        "WishlistViewModel"
                    )
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun toggleWishlist(game: Game) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result =
                toggleWishlistGameUseCase(
                    getApplication<Application>().applicationContext,
                    game
                )
            ) {
                is Resource.Success -> {
                    _error.value = null
                    // Analytics-Tracking
                    AppAnalytics.trackGameInteraction(game.id.toString(), "toggle_wishlist")
                    AppAnalytics.trackUserAction("wishlist_toggled", game.id)
                }

                is Resource.Error -> {
                    _error.value = result.message
                    // Error-Tracking
                    CrashlyticsHelper.recordWishlistError(
                        "toggle_wishlist",
                        game.id,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to toggle wishlist: ${result.message}",
                        "WishlistViewModel"
                    )
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun clearAllWishlist() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result =
                clearAllWishlistGamesUseCase(
                    getApplication<Application>().applicationContext
                )
            ) {
                is Resource.Success -> {
                    _error.value = null
                    // Analytics-Tracking
                    AppAnalytics.trackUserAction("wishlist_cleared_all")
                    AppAnalytics.trackCacheOperation(
                        "clear_wishlist",
                        _wishlistGames.value.size,
                        true
                    )
                }

                is Resource.Error -> {
                    _error.value = result.message
                    // Error-Tracking
                    CrashlyticsHelper.recordWishlistError(
                        "clear_all",
                        0,
                        result.message ?: "Unknown error"
                    )
                    AppAnalytics.trackError(
                        "Failed to clear wishlist: ${result.message}",
                        "WishlistViewModel"
                    )
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    suspend fun isInWishlist(gameId: Int): Boolean = isInWishlistUseCase(gameId)

    suspend fun getWishlistGameById(gameId: Int): Game? {
        return getWishlistGameByIdUseCase(gameId)
    }

    suspend fun getWishlistCount(): Int {
        return getWishlistCountUseCase()
    }

    fun searchWishlistGames(query: String): Flow<List<Game>> {
        return searchWishlistGamesUseCase(query)
    }

    fun exportWishlistToUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _exportResult.value = exportWishlistToUriUseCase(context, uri)
            // Analytics-Tracking
            when (_exportResult.value) {
                is Resource.Success -> {
                    AppAnalytics.trackCacheOperation(
                        "export_wishlist",
                        _wishlistGames.value.size,
                        true
                    )
                    AppAnalytics.trackUserAction("wishlist_exported")
                }

                is Resource.Error -> {
                    AppAnalytics.trackCacheOperation(
                        "export_wishlist",
                        _wishlistGames.value.size,
                        false
                    )
                    AppAnalytics.trackError("Failed to export wishlist", "WishlistViewModel")
                }

                else -> {}
            }
        }
    }

    fun importWishlistFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _importResult.value = importWishlistFromUriUseCase(context, uri)
            // Analytics-Tracking
            when (_importResult.value) {
                is Resource.Success -> {
                    AppAnalytics.trackCacheOperation(
                        "import_wishlist",
                        _wishlistGames.value.size,
                        true
                    )
                    AppAnalytics.trackUserAction("wishlist_imported")
                }

                is Resource.Error -> {
                    AppAnalytics.trackCacheOperation(
                        "import_wishlist",
                        _wishlistGames.value.size,
                        false
                    )
                    AppAnalytics.trackError("Failed to import wishlist", "WishlistViewModel")
                }

                else -> {}
            }
        }
    }
}
