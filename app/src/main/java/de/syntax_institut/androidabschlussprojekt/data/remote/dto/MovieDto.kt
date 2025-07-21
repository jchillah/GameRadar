package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für einen Trailer oder Film eines Spiels.
 * Enthält alle relevanten Informationen zu einem Video.
 *
 * @property id Eindeutige ID des Videos
 * @property name Name oder Titel des Videos
 * @property preview URL zum Vorschaubild (optional)
 * @property data Videodaten mit verschiedenen Qualitätsstufen
 */
@JsonClass(generateAdapter = true)
data class MovieDto(
    /** Eindeutige ID des Videos */
    val id: Int,
    /** Name oder Titel des Videos */
    val name: String,
    /** URL zum Vorschaubild (optional) */
    val preview: String?,
    /** Videodaten mit verschiedenen Qualitätsstufen */
    val data: MovieDataDto,
)

