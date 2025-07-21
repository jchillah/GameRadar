package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für ein Unternehmen (Entwickler oder Publisher).
 *
 * @property id Eindeutige ID des Unternehmens
 * @property name Name des Unternehmens
 */
@JsonClass(generateAdapter = true)
data class CompanyDto(
    /** Eindeutige ID des Unternehmens */
    val id: Int,
    /** Name des Unternehmens */
    val name: String,
)