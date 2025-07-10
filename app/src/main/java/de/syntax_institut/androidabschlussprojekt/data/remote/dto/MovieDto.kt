package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val name: String,
    val preview: String?,
    val data: MovieDataDto,
)

