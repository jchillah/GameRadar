package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamesResponse(
    val results: List<GameDto>,
    val next: String?,
    val previous: String?
)