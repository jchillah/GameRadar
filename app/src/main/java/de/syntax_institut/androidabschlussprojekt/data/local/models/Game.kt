package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Domain Model für ein Spiel.
 * Enthält alle relevanten Informationen, die für die Anzeige und Verarbeitung eines Spiels benötigt werden.
 *
 * @property id Eindeutige ID des Spiels
 * @property slug Slug des Spiels (für URLs oder API)
 * @property title Titel des Spiels
 * @property releaseDate Erscheinungsdatum des Spiels (optional)
 * @property imageUrl URL zum Vorschaubild (optional)
 * @property rating Bewertung des Spiels
 * @property description Beschreibung des Spiels (optional)
 * @property metacritic Metacritic-Score (optional)
 * @property website Offizielle Website des Spiels (optional)
 * @property esrbRating USK/ESRB-Altersfreigabe (optional)
 * @property genres Liste der Genres
 * @property platforms Liste der Plattformen
 * @property developers Liste der Entwickler
 * @property publishers Liste der Publisher
 * @property tags Liste der Tags
 * @property screenshots Liste der Screenshot-URLs
 * @property stores Liste der Stores
 * @property playtime Durchschnittliche Spielzeit (optional)
 * @property movies Liste der zugehörigen Trailer/Videos
 */
data class Game(
    /** Eindeutige ID des Spiels */
    val id: Int,
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
    val metacritic: Int? = null,
    /** Offizielle Website des Spiels (optional) */
    val website: String? = null,
    /** USK/ESRB-Altersfreigabe (optional) */
    val esrbRating: String? = null,
    /** Liste der Genres */
    val genres: List<String> = emptyList(),
    /** Liste der Plattformen */
    val platforms: List<String> = emptyList(),
    /** Liste der Entwickler */
    val developers: List<String> = emptyList(),
    /** Liste der Publisher */
    val publishers: List<String> = emptyList(),
    /** Liste der Tags */
    val tags: List<String> = emptyList(),
    /** Liste der Screenshot-URLs */
    val screenshots: List<String> = emptyList(),
    /** Liste der Stores */
    val stores: List<String> = emptyList(),
    /** Durchschnittliche Spielzeit (optional) */
    val playtime: Int? = null,
    /** Liste der zugehörigen Trailer/Videos */
    val movies: List<Movie> = emptyList(),
)
