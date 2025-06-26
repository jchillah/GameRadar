package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.JsonClass

data class GenreResponse(
    @Json(name = "results") val genres: List<GenreDto>
)