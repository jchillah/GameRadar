package de.syntax_institut.androidabschlussprojekt.data.remote.wrappers

import de.syntax_institut.androidabschlussprojekt.data.remote.dto.StoreDto

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class StoreWrapperDto(
    @com.squareup.moshi.Json(
        name = "store"
    )
    val store: StoreDto)