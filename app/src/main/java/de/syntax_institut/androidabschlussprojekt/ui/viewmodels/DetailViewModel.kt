package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.ui.states.DetailUiState
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**

* ViewModel für Spieldetails.
*/
class DetailViewModel(
    private val repo: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadDetail(id: Int) {
        Log.d("DetailViewModel", "Lade Spieldetails für ID: $id")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DetailUiState(isLoading = true)
            when (val res = repo.getGameDetail(id)) {
                is Resource.Success -> {
                    Log.d("DetailViewModel", "Erfolgreich geladen: ${res.data}")
                    _uiState.value = DetailUiState(game = res.data)
                }
                is Resource.Error -> {
                    Log.e("DetailViewModel", "Fehler beim Laden: ${res.message}")
                    _uiState.value = DetailUiState(error = res.message)
                }
                else -> {}
            }
        }
    }
}

