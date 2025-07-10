package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.navigation.*

/**
 * Navigiert zu einer Route ohne mehrere Instanzen zu erzeugen (SingleTop, State Restore).
 */
fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navigiert zu einem Tab und verwirft dabei den DetailScreen.
 * Verwendet für Tab-Navigation, um sicherzustellen, dass der DetailScreen geschlossen wird.
 */
fun NavController.navigateToTab(route: String) {
    // Prüfe ob wir aktuell auf dem DetailScreen sind
    val currentRoute = currentBackStackEntry?.destination?.route
    val isOnDetailScreen = currentRoute?.startsWith("detail/") == true

    this.navigate(route) {
        if (isOnDetailScreen) {
            // Wenn wir auf dem DetailScreen sind, entferne ihn komplett
            popUpTo(graph.startDestinationId) {
                saveState = true
                inclusive = false
            }
        } else {
            // Wenn wir bereits auf einem Tab sind, nur zur gewünschten Route navigieren
            popUpTo(graph.startDestinationId) {
                saveState = true
                inclusive = false
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
