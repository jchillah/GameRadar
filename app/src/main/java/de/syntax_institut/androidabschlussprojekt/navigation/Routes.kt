package de.syntax_institut.androidabschlussprojekt.navigation

/**
 * Enthält alle Routen (Screens) der App für zentrale Navigation-Verwaltung.
 *
 * Features:
 * - Zentrale Definition aller App-Routen
 * - Typsichere Navigation mit Konstanten
 * - Parameter-basierte Routen (z.B. Detail-Screen)
 * - Einfache Wartung und Änderungen
 *
 * Routen:
 * - SEARCH: Hauptsuchbildschirm
 * - FAVORITES: Favoritenliste
 * - DETAIL: Spieldetailansicht mit gameId-Parameter
 * - SETTINGS: Einstellungsbildschirm
 * - WISHLIST: Wunschliste
 * - STATS: Statistiken
 *
 * Nutze diese Konstanten für Navigation und Vergleiche.
 */
object Routes {
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val DETAIL = "detail/{gameId}"
    const val SETTINGS = "settings"
    const val WISHLIST = "wishlist"
    const val STATS = "stats"

    /**
     * Generiert eine Route für die Detailansicht mit einer konkreten Spiel-ID.
     *
     * @param gameId Die ID des Spiels für die Detailansicht
     * @return Formatierte Route-String für die Navigation
     */
    fun detail(gameId: Int) = "detail/$gameId"
}
