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
 * ViewModel für Spieldetails.
 */
class DetailViewModel(
    private val api: RawgApi
) : ViewModel(), KoinComponent {
    private val repo: GameRepository by inject()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)
            when (val res = repo.getGameDetail(id)) {
                is Resource.Success -> _uiState.value = DetailUiState(game = res.data)
                is Resource.Error -> _uiState.value = DetailUiState(error = res.message)
                else -> {}
            }
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val game: Game? = null,
    val error: String? = null
)