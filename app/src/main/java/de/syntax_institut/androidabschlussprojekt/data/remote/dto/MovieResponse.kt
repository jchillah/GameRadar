package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die API-Antwort auf eine Trailer-/Film-Abfrage.
 * Enthält die Anzahl und die Liste der gefundenen Videos.
 *
 * @property count Anzahl der gefundenen Videos
 * @property results Liste der Trailer/Filme
 */
@JsonClass(generateAdapter = true)
data class MovieResponse(
    /** Anzahl der gefundenen Videos */
    val count: Int,
    /** Liste der Trailer/Filme */
    val results: List<MovieDto>,
)