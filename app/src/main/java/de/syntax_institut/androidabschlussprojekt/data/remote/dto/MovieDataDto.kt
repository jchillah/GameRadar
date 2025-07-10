package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class MovieDataDto(
    @Json(name = "480") val low: String?,
    @Json(name = "max") val max: String?,
)