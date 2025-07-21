package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

/**
 * DTO für die USK/ESRB-Altersfreigabe eines Spiels.
 *
 * @property id Eindeutige ID der Altersfreigabe
 * @property name Name der Altersfreigabe
 */
@JsonClass(generateAdapter = true)
data class EsrbRatingDto(
    /** Eindeutige ID der Altersfreigabe */
    val id: Int,
    /** Name der Altersfreigabe */
    val name: String,
)