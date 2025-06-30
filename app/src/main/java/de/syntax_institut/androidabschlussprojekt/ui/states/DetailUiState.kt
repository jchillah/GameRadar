package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.Game

data class DetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val game: Game? = null,
    val userRating: Float = 0f
)
