package de.syntax_institut.androidabschlussprojekt.domain.models

/**
 * Repräsentiert ein Genre eines Spiels (z.B. Action, Adventure, RPG).
 *
 * @property id Eindeutige ID des Genres (RAWG API)
 * @property name Name des Genres (z.B. "Action")
 */
data class Genre(
    val id: Int,
    val name: String
)