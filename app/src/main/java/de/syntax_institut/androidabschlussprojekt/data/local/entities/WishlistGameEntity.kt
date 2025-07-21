package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.*

/**
 * Room Entity für ein Spiel in der Wunschliste.
 * Speichert alle relevanten Spieldaten, die für die Wunschlistenfunktion benötigt werden.
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
 * @property screenshots JSON-String mit Screenshots zum Spiel
 * @property movies JSON-String mit Videos/Trailern zum Spiel
 */
@Entity(tableName = "wishlist_games")
data class WishlistGameEntity(
    /** Eindeutige ID des Spiels */
    @PrimaryKey val id: Int,
    /** Slug des Spiels (für URLs oder API) */
    val slug: String,
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
    /** JSON-String mit Screenshots zum Spiel */
    val screenshots: String, // JSON-String
    /** JSON-String mit Videos/Trailern zum Spiel */
    val movies: String, // JSON-String
)
