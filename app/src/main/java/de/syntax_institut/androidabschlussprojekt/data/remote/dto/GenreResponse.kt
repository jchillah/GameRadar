package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die API-Antwort auf eine Genre-Abfrage.
 * Enthält die Liste der gefundenen Genres.
 *
 * @property genres Liste der Genres
 */
@JsonClass(generateAdapter = true)
data class GenreResponse(
    /** Liste der Genres */
    @Json(name = "results") val genres: List<GenreDto>,
)