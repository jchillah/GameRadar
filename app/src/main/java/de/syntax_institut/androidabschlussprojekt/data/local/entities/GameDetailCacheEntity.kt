package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.*

/**
 * Room Entity für gecachte Spieldetails (Detailansicht).
 * Speichert vollständige Spieldaten für die Detailansicht, unabhängig vom Paging-Cache.
 *
 * @property id Eindeutige ID des Spiels (Primary Key)
 * @property slug Slug des Spiels (für URLs oder API)
 * @property title Titel des Spiels
 * @property releaseDate Erscheinungsdatum des Spiels (optional)
 * @property imageUrl URL zum Vorschaubild (optional)
 * @property rating Bewertung des Spiels
 * @property description Beschreibung des Spiels (optional)
 * @property metacritic Metacritic-Score (optional)
 * @property website Offizielle Website des Spiels (optional)
 * @property esrbRating USK/ESRB-Altersfreigabe (optional)
 * @property genres JSON-String mit Genres
 * @property platforms JSON-String mit Plattformen
 * @property developers JSON-String mit Entwicklern
 * @property publishers JSON-String mit Publishern
 * @property tags JSON-String mit Tags
 * @property screenshots JSON-String mit Screenshots
 * @property stores JSON-String mit Stores
 * @property playtime Durchschnittliche Spielzeit (optional)
 * @property movies JSON-String mit Videos/Trailern
 * @property detailCachedAt Zeitstempel, wann die Details gecacht wurden
 */
@Entity(tableName = "game_detail_cache")
data class GameDetailCacheEntity(
    /** Eindeutige ID des Spiels */
    @PrimaryKey
    val id: Int,
    /** Slug des Spiels (für URLs oder API) */
    val slug: String = "",
    /** Titel des Spiels */
    val title: String,
    /** Erscheinungsdatum des Spiels (optional) */
    val releaseDate: String?,
    /** URL zum Vorschaubild (optional) */
    val imageUrl: String?,
    /** Bewertung des Spiels */
    val rating: Float,
    /** Beschreibung des Spiels (optional) */
    val description: String?,
    /** Metacritic-Score (optional) */
    val metacritic: Int?,
    /** Offizielle Website des Spiels (optional) */
    val website: String?,
    /** USK/ESRB-Altersfreigabe (optional) */
    val esrbRating: String?,
    /** JSON-String mit Genres */
    val genres: String,
    /** JSON-String mit Plattformen */
    val platforms: String,
    /** JSON-String mit Entwicklern */
    val developers: String,
    /** JSON-String mit Publishern */
    val publishers: String,
    /** JSON-String mit Tags */
    val tags: String,
    /** JSON-String mit Screenshots */
    val screenshots: String,
    /** JSON-String mit Stores */
    val stores: String,
    /** Durchschnittliche Spielzeit (optional) */
    val playtime: Int?,
    /** JSON-String mit Videos/Trailern */
    val movies: String,
    /** Zeitstempel, wann die Details gecacht wurden */
    val detailCachedAt: Long = System.currentTimeMillis(),
) 