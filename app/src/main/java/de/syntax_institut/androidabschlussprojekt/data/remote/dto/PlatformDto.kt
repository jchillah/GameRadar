package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlatformDto(
    val id: Int,
    val name: String
)