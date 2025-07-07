package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

data class DetailUiState(
    val resource: Resource<Game>? = null,
    val error: String? = null,
    val game: Game? = null,
    val userRating: Float = 0f,
)
