package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UI-State für die Spieldetailansicht mit allen relevanten Zuständen.
 *
 * Enthält:
 * - Loading- und Error-States
 * - Spieldaten
 * - Benutzerinteraktionen (Favoriten, Wunschliste, Bewertung)
 * - Resource-Wrapper für API-Antworten
 *
 * @param resource Resource-Wrapper für die API-Antwort
 * @param error Fehlermeldung als String
 * @param errorMessageId Fehlermeldung als String-Ressourcen-ID
 * @param game Das anzuzeigende Spiel
 * @param userRating Benutzerbewertung des Spiels
 * @param isLoading Gibt an, ob Spieldaten geladen werden
 * @param isFavorite Gibt an, ob das Spiel in den Favoriten ist
 * @param isInWishlist Gibt an, ob das Spiel in der Wunschliste ist
 */
data class DetailUiState(
    val resource: Resource<Game>? = null,
    val error: String? = null,
    val errorMessageId: Int? = null,
    val game: Game? = null,
    val userRating: Float = 0f,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val isInWishlist: Boolean = false,
)
