package de.syntax_institut.androidabschlussprojekt.navigation

sealed class Routes(val route: String) {
    object Home    : Routes("home")
    object Settings: Routes("settings")
}