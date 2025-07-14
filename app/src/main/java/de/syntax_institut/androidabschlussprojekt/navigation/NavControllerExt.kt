package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.navigation.*

/** Navigiert zu einer Route ohne mehrere Instanzen zu erzeugen (SingleTop, State Restore). */
fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navigiert zu einem Tab und verwirft dabei den DetailScreen. Verwendet für Tab-Navigation, um
 * sicherzustellen, dass der DetailScreen geschlossen wird.
 */
fun NavController.navigateToTab(route: String) {
    // Prüfe ob wir aktuell auf dem DetailScreen sind
    val currentRoute = currentBackStackEntry?.destination?.route
    val isOnDetailScreen = currentRoute?.startsWith("detail/") == true

    this.navigate(route) {
        // Immer bis zum Start-Tab poppen, aber wenn wir auf einem DetailScreen sind, inclusive =
        // true
        popUpTo(graph.startDestinationId) {
            saveState = true
            inclusive = isOnDetailScreen
        }
        launchSingleTop = true
        restoreState = true
    }
}
