package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für ein Genre eines Spiels.
 *
 * @property id Eindeutige ID des Genres
 * @property name Name des Genres
 */
@JsonClass(generateAdapter = true)
data class GenreDto(
    /** Eindeutige ID des Genres */
    val id: Int,
    /** Name des Genres */
    val name: String,
)