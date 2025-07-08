package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.*

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Game> = emptyList(),
    val error: String? = null
) 