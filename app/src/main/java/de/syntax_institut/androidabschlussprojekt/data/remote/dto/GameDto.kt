package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.remote.wrappers.*

@JsonClass(generateAdapter = true)
data class GameDto(
    val id: Int,
    val slug: String,
    val name: String,
    @Json(name = "released") val released: String?,
    @Json(name = "background_image") val backgroundImage: String?,
    val rating: Float,
    @Json(name = "description_raw") val description: String? = null,
    val metacritic: Int? = null,
    val website: String? = null,
    @Json(name = "esrb_rating") val esrbRating: EsrbRatingDto? = null,
    val genres: List<GenreDto>? = null,
    val platforms: List<PlatformWrapperDto>? = null,
    val developers: List<CompanyDto>? = null,
    val publishers: List<CompanyDto>? = null,
    val tags: List<TagDto>? = null,
    @Json(name = "short_screenshots") val shortScreenshots: List<ScreenshotDto>? = null,
    val stores: List<StoreWrapperDto>? = null,
    val playtime: Int? = null,
)