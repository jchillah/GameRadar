package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * UI-State für die Favoritenliste mit allen relevanten Zuständen.
 *
 * Enthält:
 * - Loading- und Error-States
 * - Liste der favorisierten Spiele
 * - Export/Import-Ergebnisse
 * - Fehlerbehandlung
 *
 * @param isLoading Gibt an, ob Favoriten geladen werden
 * @param favorites Liste der favorisierten Spiele
 * @param error Fehlermeldung beim Laden der Favoriten
 * @param exportSuccess Gibt an, ob Export erfolgreich war
 * @param exportMessage Nachricht zum Export-Ergebnis
 * @param importSuccess Gibt an, ob Import erfolgreich war
 * @param importMessage Nachricht zum Import-Ergebnis
 */
data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Game> = emptyList(),
    val error: String? = null,
    val exportSuccess: Boolean? = null,
    val exportMessage: String? = null,
    val importSuccess: Boolean? = null,
    val importMessage: String? = null,
) 