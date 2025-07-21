package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die API-Antwort auf eine Spiele-Abfrage.
 * Enthält die Liste der gefundenen Spiele und die Paginierungs-Links.
 *
 * @property results Liste der Spiele
 * @property next Link zur nächsten Seite (optional)
 * @property previous Link zur vorherigen Seite (optional)
 */
@JsonClass(generateAdapter = true)
data class GamesResponse(
    /** Liste der Spiele */
    val results: List<GameDto>,
    /** Link zur nächsten Seite (optional) */
    val next: String?,
    /** Link zur vorherigen Seite (optional) */
    val previous: String?,
)