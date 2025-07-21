package de.syntax_institut.androidabschlussprojekt.data.remote.dto

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.remote.wrappers.*

/**
 * DTO für ein Spiel aus der RAWG-API.
 * Enthält alle relevanten Felder, die von der API geliefert werden.
 *
 * @property id Eindeutige ID des Spiels
 * @property slug Slug des Spiels (für URLs oder API)
 * @property name Name/Titel des Spiels
 * @property released Erscheinungsdatum (optional)
 * @property backgroundImage URL zum Vorschaubild (optional)
 * @property rating Bewertung des Spiels
 * @property description Beschreibung des Spiels (optional)
 * @property metacritic Metacritic-Score (optional)
 * @property website Offizielle Website (optional)
 * @property esrbRating USK/ESRB-Altersfreigabe (optional)
 * @property genres Liste der Genres (optional)
 * @property platforms Liste der Plattformen (optional)
 * @property developers Liste der Entwickler (optional)
 * @property publishers Liste der Publisher (optional)
 * @property tags Liste der Tags (optional)
 * @property shortScreenshots Liste der Vorschaubilder (optional)
 * @property stores Liste der Stores (optional)
 * @property playtime Durchschnittliche Spielzeit (optional)
 */
@JsonClass(generateAdapter = true)
data class GameDto(
    /** Eindeutige ID des Spiels */
    val id: Int,
    /** Slug des Spiels (für URLs oder API) */
    val slug: String,
    /** Name/Titel des Spiels */
    val name: String,
    /** Erscheinungsdatum (optional) */
    @Json(name = "released") val released: String?,
    /** URL zum Vorschaubild (optional) */
    @Json(name = "background_image") val backgroundImage: String?,
    /** Bewertung des Spiels */
    val rating: Float,
    /** Beschreibung des Spiels (optional) */
    @Json(name = "description_raw") val description: String? = null,
    /** Metacritic-Score (optional) */
    val metacritic: Int? = null,
    /** Offizielle Website (optional) */
    val website: String? = null,
    /** USK/ESRB-Altersfreigabe (optional) */
    @Json(name = "esrb_rating") val esrbRating: EsrbRatingDto? = null,
    /** Liste der Genres (optional) */
    val genres: List<GenreDto>? = null,
    /** Liste der Plattformen (optional) */
    val platforms: List<PlatformWrapperDto>? = null,
    /** Liste der Entwickler (optional) */
    val developers: List<CompanyDto>? = null,
    /** Liste der Publisher (optional) */
    val publishers: List<CompanyDto>? = null,
    /** Liste der Tags (optional) */
    val tags: List<TagDto>? = null,
    /** Liste der Vorschaubilder (optional) */
    @Json(name = "short_screenshots") val shortScreenshots: List<ScreenshotDto>? = null,
    /** Liste der Stores (optional) */
    val stores: List<StoreWrapperDto>? = null,
    /** Durchschnittliche Spielzeit (optional) */
    val playtime: Int? = null,
)