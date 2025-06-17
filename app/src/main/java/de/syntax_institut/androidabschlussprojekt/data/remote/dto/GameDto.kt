package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameDto(
    val id: Int,
    val name: String,
    @Json(name = "released") val released: String?,
    @Json(name = "background_image") val backgroundImage: String?,
    val rating: Float,
    @Json(name = "description_raw") val description: String? = null
)
