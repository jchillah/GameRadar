package de.syntax_institut.androidabschlussprojekt.data.local.models

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class Movie(
    val id: Int,
    val name: String,
    val preview: String?,
    val url480: String?,
    val urlMax: String?,
)
