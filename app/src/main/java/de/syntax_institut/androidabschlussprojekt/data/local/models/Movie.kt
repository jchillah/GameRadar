package de.syntax_institut.androidabschlussprojekt.data.local.models

import com.squareup.moshi.*

/**
 * Datenklasse für einen Spiel-Trailer oder -Film.
 * Enthält alle relevanten Informationen zu einem Video, das einem Spiel zugeordnet ist.
 *
 * @property id Eindeutige ID des Videos
 * @property name Name oder Titel des Videos
 * @property preview URL zum Vorschaubild (optional)
 * @property url480 URL zur 480p-Version des Videos (optional)
 * @property urlMax URL zur maximalen Qualitätsstufe des Videos (optional)
 */
@JsonClass(generateAdapter = true)
data class Movie(
    /** Eindeutige ID des Videos */
    val id: Int,
    /** Name oder Titel des Videos */
    val name: String,
    /** URL zum Vorschaubild (optional) */
    val preview: String?,
    /** URL zur 480p-Version des Videos (optional) */
    val url480: String?,
    /** URL zur maximalen Qualitätsstufe des Videos (optional) */
    val urlMax: String?,
)
