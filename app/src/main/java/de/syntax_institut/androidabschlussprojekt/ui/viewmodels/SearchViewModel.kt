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

    fun search(query: String) {
        Log.d("SearchViewModel", "Search gestartet mit Query: $query")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = SearchUiState(isLoading = true)
            when (val res = repo.searchGames(query)) {
                is Resource.Success -> {
                    Log.d("SearchViewModel", "Erfolgreiche Suche: ${res.data?.size} Ergebnisse")
                    _uiState.value = SearchUiState(games = res.data ?: emptyList())
                }
                is Resource.Error -> {
                    Log.e("SearchViewModel", "Fehler bei der Suche: ${res.message}")
                    _uiState.value = SearchUiState(error = res.message)
                }
                else -> {}
            }
        }
    }

    fun updateFilters(platforms: List<String>, genres: List<String>, rating: Float) {
        _uiState.update { it.copy(
            selectedPlatforms = platforms,
            selectedGenres = genres,
            rating = rating
        )}
    }
}
