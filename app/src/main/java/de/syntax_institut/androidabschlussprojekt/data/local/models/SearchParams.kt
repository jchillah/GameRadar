package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Datenklasse für Suchparameter bei der Spielsuche.
 * Enthält alle Filter- und Sortieroptionen für eine Suchanfrage.
 *
 * @property query Suchbegriff
 * @property platforms Plattform-Filter (optional)
 * @property genres Genre-Filter (optional)
 * @property ordering Sortierreihenfolge (optional)
 */
data class SearchParams(
    /** Suchbegriff */
    val query: String = "",
    /** Plattform-Filter (optional) */
    val platforms: String? = null,
    /** Genre-Filter (optional) */
    val genres: String? = null,
    /** Sortierreihenfolge (optional) */
    val ordering: String? = null,
)