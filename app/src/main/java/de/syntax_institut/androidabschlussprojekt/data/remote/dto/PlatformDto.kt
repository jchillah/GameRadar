package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für eine Plattform (z.B. PC, PlayStation, Xbox).
 *
 * @property id Eindeutige ID der Plattform
 * @property name Name der Plattform
 */
@JsonClass(generateAdapter = true)
data class PlatformDto(
    /** Eindeutige ID der Plattform */
    val id: Int,
    /** Name der Plattform */
    val name: String,
)