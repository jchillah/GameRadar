package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für ein Tag eines Spiels.
 *
 * @property id Eindeutige ID des Tags
 * @property name Name des Tags
 */
@JsonClass(generateAdapter = true)
data class TagDto(
    /** Eindeutige ID des Tags */
    val id: Int,
    /** Name des Tags */
    val name: String,
)