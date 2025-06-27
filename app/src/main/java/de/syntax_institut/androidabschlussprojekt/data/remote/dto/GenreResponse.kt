package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreResponse(
    @Json(name = "results") val genres: List<GenreDto>
)