package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.util.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**

* ViewModel für die Suche.
*/
class SearchViewModel(
    private val repo: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        loadFilterOptions()
    }

    fun search(query: String) {
        Log.d("SearchViewModel", "Search gestartet mit Query: $query")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val currentFilters = _uiState.value

            when (val res = repo.searchGames(
 query = query,
 platforms = currentFilters.selectedPlatformIds, // Use stored platform IDs
 genres = currentFilters.selectedGenreIds, // Use stored genre IDs
 rating = currentFilters.rating // Use stored rating
            )) {
                is Resource.Success -> {
                    Log.d("SearchViewModel", "Erfolgreiche Suche: ${res.data?.size} Ergebnisse")
                    _uiState.update { it.copy(games = res.data ?: emptyList(), isLoading = false, error = null) }
                }
                is Resource.Error -> {
                    Log.e("SearchViewModel", "Fehler bei der Suche: ${res.message}")
                    _uiState.value = SearchUiState(error = res.message)
                }
                else -> {}
            }
        }
    }

    fun updateFilters(selectedPlatformNames: List<String>, selectedGenreNames: List<String>, rating: Float) {
        // Map the selected names back to IDs for the API call.
        // This assumes that the `availablePlatforms` and `availableGenres` in the state
        // are lists of strings representing the names, and we need to find their corresponding IDs.
        // In a real application, you would likely want to store available platforms and genres
        // as data classes containing both name and ID.
        val platformMap = _uiState.value.availablePlatforms.associateWith { /* Add logic to get platform ID by name */ 0 } // TODO: Implement platform name to ID mapping
 val genreMap = _uiState.value.availableGenres.associateWith { /* Add logic to get genre ID by name */ 0 } // TODO: Implement genre name to ID mapping

        _uiState.update {
            it.copy(
 selectedPlatformNames = selectedPlatformNames,
 selectedGenreNames = selectedGenreNames,
 selectedPlatformIds = selectedPlatformNames.map { platformMap[it] ?: 0 }, // TODO: Use actual mapped IDs
 selectedGenreIds = selectedGenreNames.map { genreMap[it] ?: 0 }, // TODO: Use actual mapped IDs
            )
        }
        // Trigger search with updated filters
        search(_uiState.value.searchQuery) // Assuming you store the last search query in the state
    }

    private fun loadFilterOptions() {
        viewModelScope.launch(Dispatchers.IO) {
            // Load platforms
            when (val res = repo.getPlatforms()) {
                is Resource.Success -> _uiState.update { it.copy(availablePlatforms = res.data ?: emptyList()) }
                is Resource.Error -> Log.e("SearchViewModel", "Error loading platforms: ${res.message}")
                else -> {}
            }
            // Load genres
            when (val res = repo.getGenres()) {
                is Resource.Success -> _uiState.update { it.copy(availableGenres = res.data ?: emptyList()) }
                is Resource.Error -> Log.e("SearchViewModel", "Error loading genres: ${res.message}")
                else -> {}
            }
        }
    }
}
