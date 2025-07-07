package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val name: String,
    val preview: String?,
    val data: MovieDataDto,
)

@JsonClass(generateAdapter = true)
data class MovieDataDto(
    @Json(name = "480") val low: String?,
    @Json(name = "max") val max: String?,
)

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val count: Int,
    val results: List<MovieDto>,
) 