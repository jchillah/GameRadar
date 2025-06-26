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
 private val _selectedPlatformIds = MutableStateFlow<List<Int>>(emptyList())
 private val _selectedGenreIds = MutableStateFlow<List<Int>>(emptyList())
 private val _rating = MutableStateFlow<Float>(0f)
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
                platforms = _selectedPlatformIds.value, // Use stored platform IDs
                genres = _selectedGenreIds.value, // Use stored genre IDs
                rating = _rating.value // Use stored rating
                           )) {
                               is Resource.Success -> {
                                   Log.d("SearchViewModel", "Erfolgreiche Suche: ${res.data?.size} Ergebnisse")
               }
                is Resource.Error -> {
                    Log.e("SearchViewModel", "Fehler bei der Suche: ${res.message}")
                    _uiState.value = SearchUiState(error = res.message)
                }
                else -> {}
            }
        }
    }

    fun updateFilters(selectedPlatformIds: List<Int>, selectedGenreIds: List<Int>, rating: Float) {
        _selectedPlatformIds.value = selectedPlatformIds
        _selectedGenreIds.value = selectedGenreIds
        _rating.value = rating
               // Trigger search with updated filters and store in state
               search(_uiState.value.searchQuery)
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
