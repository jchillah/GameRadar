package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class ScreenshotDto(
    val id: Int,
    val image: String,
)

data class ScreenshotResponse(
    val results: List<ScreenshotDto>,
)