package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.domain.models.*

/**
 * UI-State für den Suchbildschirm mit allen relevanten Zuständen und Filtern.
 *
 * Enthält:
 * - Loading- und Error-States
 * - Suchfilter (Plattformen, Genres, Bewertung, Sortierung)
 * - Verfügbare Filter-Optionen
 * - Cache-Informationen
 * - Offline-Status
 * - Suchhistorie
 *
 * @param isLoading Gibt an, ob eine Suche läuft
 * @param error Fehlermeldung als String
 * @param errorMessageId Fehlermeldung als String-Ressourcen-ID
 * @param selectedPlatforms Liste der ausgewählten Plattform-IDs
 * @param selectedGenres Liste der ausgewählten Genre-IDs
 * @param rating Mindestbewertung für die Suche
 * @param ordering Sortierreihenfolge
 * @param platforms Verfügbare Plattformen
 * @param genres Verfügbare Genres
 * @param hasSearched Gibt an, ob bereits gesucht wurde
 * @param isOffline Gibt an, ob die App offline ist
 * @param cacheSize Aktuelle Cache-Größe
 * @param lastSyncTime Zeitstempel der letzten Synchronisation
 * @param isLoadingPlatforms Gibt an, ob Plattformen geladen werden
 * @param isLoadingGenres Gibt an, ob Genres geladen werden
 * @param platformsError Fehlermeldung beim Laden der Plattformen
 * @param platformsErrorId Fehlermeldung-ID für Plattformen
 * @param genresError Fehlermeldung beim Laden der Genres
 * @param genresErrorId Fehlermeldung-ID für Genres
 * @param isApplyingFilters Gibt an, ob Filter angewendet werden
 * @param filterError Fehlermeldung beim Anwenden von Filtern
 */
data class SearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorMessageId: Int? = null,
    val selectedPlatforms: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val rating: Float = 0f,
    val ordering: String = "",
    val platforms: List<Platform> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val hasSearched: Boolean = false,
    val isOffline: Boolean = false,
    val cacheSize: Int = 0,
    val lastSyncTime: Long? = null,
    val isLoadingPlatforms: Boolean = false,
    val isLoadingGenres: Boolean = false,
    val platformsError: String? = null,
    val platformsErrorId: Int? = null,
    val genresError: String? = null,
    val genresErrorId: Int? = null,
    val isApplyingFilters: Boolean = false,
    val filterError: String? = null,
)
