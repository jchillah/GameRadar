package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.JsonClass

data class PlatformResponse(
    @Json(name = "results") val platforms: List<PlatformDto>
)