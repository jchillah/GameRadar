package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die API-Antwort auf eine Plattform-Abfrage.
 * Enthält die Liste der gefundenen Plattformen.
 *
 * @property platforms Liste der Plattformen
 */
@JsonClass(generateAdapter = true)
data class PlatformResponse(
    /** Liste der Plattformen */
    @Json(name = "results") val platforms: List<PlatformDto>,
)