package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.Game

data class SearchUiState(
    val isLoading: Boolean = false,
    val games: List<Game> = emptyList(),
    val error: String? = null,
    val selectedPlatforms: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val rating: Float = 0f
)
