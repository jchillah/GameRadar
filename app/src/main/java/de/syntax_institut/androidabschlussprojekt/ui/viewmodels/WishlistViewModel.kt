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
 * ViewModel für die Wunschliste. Verwaltet alle UI-States, Datenoperationen und Export/Import-Logik
 * für die Wishlist.
 *
 * - Kapselt alle Interaktionen mit den UseCases für Wishlist-Funktionen.
 * - Hält State für die UI (Laden, Fehler, Export/Import-Status, Detailspiel).
 * - Keine Context-Logik in der UI, alles über UseCases und Repository.
 *
 * @param addWishlistGameUseCase UseCase zum Hinzufügen eines Spiels zur Wunschliste
 * @param removeWishlistGameUseCase UseCase zum Entfernen eines Spiels aus der Wunschliste
 * @param toggleWishlistGameUseCase UseCase zum Umschalten des Wishlist-Status
 * @param getAllWishlistGamesUseCase UseCase zum Laden aller Wunschlistenspiele
 * @param clearAllWishlistGamesUseCase UseCase zum Leeren der Wunschliste
 * @param isInWishlistUseCase UseCase zur Prüfung, ob ein Spiel auf der Wunschliste ist
 * @param getWishlistGameByIdUseCase UseCase zum Laden eines bestimmten Wunschlistenspiels
 * @param getWishlistCountUseCase UseCase zum Zählen der Wunschlistenspiele
 * @param searchWishlistGamesUseCase UseCase für die Suche in der Wunschliste
 * @param exportWishlistToUriUseCase UseCase für den Export der Wunschliste
 * @param importWishlistFromUriUseCase UseCase für den Import der Wunschliste
 * @param application Anwendungskontext (für AndroidViewModel)
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
                is Resource.Success -> _error.value = null
                is Resource.Error -> _error.value = result.message
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
                is Resource.Success -> _error.value = null
                is Resource.Error -> _error.value = result.message
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
                is Resource.Success -> _error.value = null
                is Resource.Error -> _error.value = result.message
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
                is Resource.Success -> _error.value = null
                is Resource.Error -> _error.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    suspend fun isInWishlist(gameId: Int): Boolean = isInWishlistUseCase(gameId)

    suspend fun getWishlistCount(): Int = getWishlistCountUseCase()

    fun searchWishlist(query: String): StateFlow<List<Game>> {
        val result = MutableStateFlow<List<Game>>(emptyList())
        viewModelScope.launch {
            searchWishlistGamesUseCase(query).collect { games -> result.value = games }
        }
        return result
    }

    fun exportWishlistToUri(context: Context, uri: Uri) {
        viewModelScope.launch { _exportResult.value = exportWishlistToUriUseCase(context, uri) }
    }

    fun importWishlistFromUri(context: Context, uri: Uri) {
        viewModelScope.launch { _importResult.value = importWishlistFromUriUseCase(context, uri) }
    }

    // Entferne die ungenutzte Funktion getWishlistGameById
    // Passe getWishlistGameById an, damit das Ergebnis im State landet
    fun loadWishlistGameById(gameId: Int) {
        viewModelScope.launch {
            val game = getWishlistGameByIdUseCase(gameId)
            setDetailGame(game)
        }
    }
}
