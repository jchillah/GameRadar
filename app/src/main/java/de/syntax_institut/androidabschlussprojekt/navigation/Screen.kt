package de.syntax_institut.androidabschlussprojekt.navigation

sealed class Screen(val route: String) {
    object Home    : Screen("home")
    object Settings: Screen("settings")
}