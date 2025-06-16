package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * ViewModel für die Suche.
 */
class SearchViewModel(
    private val repo: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState(isLoading = true)
            when (val res = repo.searchGames(query)) {
                is Resource.Success -> _uiState.value = SearchUiState(games = res.data ?: emptyList())
                is Resource.Error -> _uiState.value = SearchUiState(error = res.message)
                else -> {}
            }
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val games: List<Game> = emptyList(),
    val error: String? = null
)