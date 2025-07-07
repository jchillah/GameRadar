package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import kotlinx.coroutines.flow.*

class TrailerPlayerViewModel : ViewModel() {
    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    private val _playWhenReady = MutableStateFlow(true)
    val playWhenReady: StateFlow<Boolean> = _playWhenReady

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun openTrailer(movie: Movie) {
        _currentMovie.value = movie
        _playbackPosition.value = 0L
        _playWhenReady.value = true
        _showDialog.value = true
        _error.value = null
    }

    fun closeDialog() {
        _showDialog.value = false
        _playbackPosition.value = 0L
        _playWhenReady.value = true
        _currentMovie.value = null
        _error.value = null
    }

    fun updatePlaybackPosition(position: Long) {
        _playbackPosition.value = position
    }

    fun setPlayWhenReady(ready: Boolean) {
        _playWhenReady.value = ready
    }

    fun setError(message: String?) {
        _error.value = message
    }

    fun savePlayerState(position: Long, playWhenReady: Boolean) {
        _playbackPosition.value = position
        _playWhenReady.value = playWhenReady
    }
} 