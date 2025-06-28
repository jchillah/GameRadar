package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.util.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import androidx.paging.PagingData
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.domain.models.Platform
import de.syntax_institut.androidabschlussprojekt.domain.models.Genre
import androidx.paging.cachedIn
import de.syntax_institut.androidabschlussprojekt.data.local.models.SearchParams

/**

* ViewModel für die Suche.
*/
class SearchViewModel(
    private val repo: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    var platforms: List<Platform> = emptyList()
    var genres: List<Genre> = emptyList()

    // Paging-Flow für die UI
    private val _pagingFlow = MutableStateFlow<PagingData<Game>>(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Game>> = _pagingFlow.asStateFlow()

    private val _searchParams = MutableStateFlow(SearchParams())
    
    // Aktueller Suchtext für automatische Neuausführung
    private var currentSearchQuery: String = ""

    init {
        val platformsList = listOf(
            Platform(1, "PC"),
            Platform(5, "macOS"),
            Platform(6, "Linux"),
            Platform(4, "iOS"),
            Platform(8, "Android"),
            Platform(39, "iPad"),
            Platform(40, "Android Tablet"),
            Platform(2, "PlayStation"),
            Platform(16, "PlayStation 3"),
            Platform(18, "PlayStation 4"),
            Platform(23, "PlayStation 5"),
            Platform(22, "PlayStation 2"),
            Platform(27, "PlayStation 1"),
            Platform(37, "PlayStation Vita"),
            Platform(38, "PSP"),
            Platform(3, "Xbox"),
            Platform(19, "Xbox 360"),
            Platform(21, "Xbox One"),
            Platform(24, "Xbox Series S/X"),
            Platform(7, "Nintendo Switch"),
            Platform(33, "Wii"),
            Platform(34, "Wii U"),
            Platform(35, "Nintendo DS"),
            Platform(36, "Nintendo 3DS"),
            Platform(32, "Nintendo 64"),
            Platform(31, "GameCube"),
            Platform(30, "Game Boy Advance"),
            Platform(28, "Game Boy"),
            Platform(29, "SNES"),
            Platform(14, "Web")
        )
        val genresList = listOf(
            Genre(1, "Action"),
            Genre(2, "Adventure"),
            Genre(3, "RPG"),
            Genre(4, "Strategy")
        )
        _uiState.update { it.copy(platforms = platformsList, genres = genresList) }
    }

    fun search(query: String) {
        Log.d("SearchViewModel", "Paging-Search gestartet mit Query: $query")
        currentSearchQuery = query
        val state = _uiState.value
        val platformIds = state.selectedPlatforms.joinToString(",")
        val genreIds = state.selectedGenres.joinToString(",")
        val ordering = state.ordering
        val rating = if (state.rating > 0f) state.rating else null
        _uiState.update { it.copy(hasSearched = true) }
        _searchParams.value = SearchParams(
            query = query,
            platforms = if (platformIds.isNotBlank()) platformIds else null,
            genres = if (genreIds.isNotBlank()) genreIds else null,
            ordering = if (ordering.isNotBlank()) ordering else null
        )
        viewModelScope.launch {
            repo.getPagedGames(
                query = _searchParams.value.query,
                platforms = _searchParams.value.platforms,
                genres = _searchParams.value.genres,
                ordering = _searchParams.value.ordering,
                rating = rating
            )
            .cachedIn(viewModelScope)
            .collect {
                _pagingFlow.value = it
            }
        }
    }

    fun updateFilters(platforms: List<String>, genres: List<String>, rating: Float) {
        _uiState.update { it.copy(
            selectedPlatforms = platforms,
            selectedGenres = genres,
            rating = rating
        )}
        
        // Automatisch die Suche neu ausführen, wenn ein Suchtext vorhanden ist
        if (currentSearchQuery.isNotBlank()) {
            search(currentSearchQuery)
        }
    }

    fun updateOrdering(ordering: String) {
        _uiState.update { it.copy(ordering = ordering) }
        
        // Automatisch die Suche neu ausführen, wenn ein Suchtext vorhanden ist
        if (currentSearchQuery.isNotBlank()) {
            search(currentSearchQuery)
        }
    }
}
