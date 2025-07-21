package de.syntax_institut.androidabschlussprojekt.data.remote.wrappers

import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*

/**
 * Wrapper-DTO für eine Plattform, wie sie von der RAWG-API verschachtelt geliefert wird.
 *
 * @property platform Die eigentliche Plattform (PlatformDto)
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
data class PlatformWrapperDto(
    /** Die eigentliche Plattform (PlatformDto) */
    @com.squareup.moshi.Json(
        name = "platform"
    )
    val platform: PlatformDto,
)