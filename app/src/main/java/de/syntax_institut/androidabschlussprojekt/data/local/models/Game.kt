package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Domain Model für ein Spiel.
 */
data class Game(
    val id: Int,
    val slug: String,
    val title: String,
    val releaseDate: String?,
    val imageUrl: String?,
    val rating: Float,
    val description: String?,
    val metacritic: Int? = null,
    val website: String? = null,
    val esrbRating: String? = null,
    val genres: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val screenshots: List<String> = emptyList(),
    val stores: List<String> = emptyList(),
    val playtime: Int? = null,
)
