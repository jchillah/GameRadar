package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*

/**
 * Room Entity für Favoriten-Spiele.
 * Speichert alle wichtigen Spieldaten lokal für Offline-Zugriff.
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
 * @property addedAt Zeitstempel, wann das Spiel zu den Favoriten hinzugefügt wurde
 */
@Entity(tableName = Constants.FAVORITE_GAME_TABLE)
data class FavoriteGameEntity(
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
    /** Zeitstempel, wann das Spiel zu den Favoriten hinzugefügt wurde */
    val addedAt: Long = System.currentTimeMillis(),
) 