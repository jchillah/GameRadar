package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für einen Screenshot eines Spiels.
 * Enthält die ID und die Bild-URL.
 *
 * @property id Eindeutige ID des Screenshots
 * @property image URL zum Screenshot-Bild
 */
@JsonClass(generateAdapter = true)
data class ScreenshotDto(
    /** Eindeutige ID des Screenshots */
    val id: Int,
    /** URL zum Screenshot-Bild */
    val image: String,
)