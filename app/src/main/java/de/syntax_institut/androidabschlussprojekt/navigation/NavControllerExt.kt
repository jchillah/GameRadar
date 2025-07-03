package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.navigation.*

/**
 * Navigiert zu einer Route ohne mehrere Instanzen zu erzeugen.
 */
fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
