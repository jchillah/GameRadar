package de.syntax_institut.androidabschlussprojekt.navigation

object Routes {
    const val SEARCH = "search"
    const val DETAIL = "detail/{gameId}"

    fun detail(gameId: Int) = "detail/$gameId"
} 