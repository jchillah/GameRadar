package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für einen Store (z.B. Steam, Epic Games Store).
 *
 * @property id Eindeutige ID des Stores
 * @property name Name des Stores
 */
@JsonClass(generateAdapter = true)
data class StoreDto(
    /** Eindeutige ID des Stores */
    val id: Int,
    /** Name des Stores */
    val name: String,
)