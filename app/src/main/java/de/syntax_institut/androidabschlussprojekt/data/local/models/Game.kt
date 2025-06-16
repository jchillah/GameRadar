package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Domain Model für ein Spiel.
 */
data class Game(
    val id: Int,
    val title: String,
    val releaseDate: String?,
    val imageUrl: String?,
    val rating: Float,
    val description: String?
)
