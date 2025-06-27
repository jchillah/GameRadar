package de.syntax_institut.androidabschlussprojekt.data.remote.wrappers

import de.syntax_institut.androidabschlussprojekt.data.remote.dto.PlatformDto

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class PlatformWrapperDto(
    @com.squareup.moshi.Json(
        name = "platform"
    )
    val platform: PlatformDto)