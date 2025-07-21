package de.syntax_institut.androidabschlussprojekt.domain.models

/**
 * Repräsentiert eine Plattform, auf der ein Spiel verfügbar ist (z.B. PC, PlayStation, Xbox).
 *
 * @property id Eindeutige ID der Plattform (RAWG API)
 * @property name Name der Plattform (z.B. "PC")
 */
data class Platform(
    val id: Int,
    val name: String
)