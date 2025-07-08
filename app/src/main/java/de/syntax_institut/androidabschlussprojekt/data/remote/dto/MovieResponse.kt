package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val count: Int,
    val results: List<MovieDto>,
)