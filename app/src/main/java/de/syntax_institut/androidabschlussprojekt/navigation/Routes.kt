package de.syntax_institut.androidabschlussprojekt.navigation

/**
 * Enthält alle Routen (Screens) der App.
 * Nutze diese Konstanten für Navigation und Vergleiche.
 */
object Routes {
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val DETAIL = "detail/{gameId}"
    const val SETTINGS = "settings"

    /**
     * Generiert eine Route für die Detailansicht mit einer konkreten Spiel-ID
     */
    fun detail(gameId: Int) = "detail/$gameId"
}
