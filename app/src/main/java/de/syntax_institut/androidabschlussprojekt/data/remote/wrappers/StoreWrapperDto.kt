package de.syntax_institut.androidabschlussprojekt.data.remote.wrappers

import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*

/**
 * Wrapper-DTO für einen Store, wie er von der RAWG-API verschachtelt geliefert wird.
 *
 * @property store Der eigentliche Store (StoreDto)
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
data class StoreWrapperDto(
    /** Der eigentliche Store (StoreDto) */
    @com.squareup.moshi.Json(
        name = "store"
    )
    val store: StoreDto,
)