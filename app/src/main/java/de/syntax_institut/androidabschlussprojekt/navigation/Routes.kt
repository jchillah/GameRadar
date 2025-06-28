package de.syntax_institut.androidabschlussprojekt.navigation

object Routes {
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val DETAIL = "detail/{gameId}"
    const val HOME = "home"

    fun detail(gameId: Int) = "detail/$gameId"
} 