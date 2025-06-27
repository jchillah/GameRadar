package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlatformResponse(
    @Json(name = "results") val platforms: List<PlatformDto>
)