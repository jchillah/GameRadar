package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die API-Antwort auf eine Screenshot-Abfrage.
 * Enthält die Liste der gefundenen Screenshots.
 *
 * @property results Liste der Screenshots
 */
@JsonClass(generateAdapter = true)
data class ScreenshotResponse(
    /** Liste der Screenshots */
    val results: List<ScreenshotDto>,
)