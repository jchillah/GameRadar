package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für Videodaten eines Trailers oder Films.
 * Enthält die URLs für verschiedene Qualitätsstufen.
 *
 * @property low URL zur 480p-Version (optional)
 * @property max URL zur maximalen Qualitätsstufe (optional)
 */
@JsonClass(generateAdapter = true)
data class MovieDataDto(
    /** URL zur 480p-Version (optional) */
    @Json(name = "480") val low: String?,
    /** URL zur maximalen Qualitätsstufe (optional) */
    @Json(name = "max") val max: String?,
)