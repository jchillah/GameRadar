package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Erweiterte Navigation-Funktionen für NavController.
 *
 * Bietet sichere Navigation mit Fehlerbehandlung und Crashlytics-Integration. Alle
 * Navigation-Operationen werden geloggt und Fehler werden automatisch aufgezeichnet.
 */

/**
 * Navigiert zu einer Route ohne mehrere Instanzen zu erzeugen (SingleTop, State Restore).
 *
 * Features:
 * - Verhindert Duplikate in der BackStack
 * - Stellt den UI-State wieder her
 * - Optimiert für Tab-Navigation
 * - Speichert den aktuellen Zustand
 * - Robuste Fehlerbehandlung mit Crashlytics
 * - Automatisches Logging aller Navigation-Events
 *
 * @param route Die Ziel-Route für die Navigation
 */
fun NavController.navigateSingleTopTo(route: String) {
    try {
        this.navigate(route) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
        AppLogger.d("Navigation", "Navigation zu: $route")
    } catch (e: Exception) {
        AppLogger.e("Navigation", "Fehler bei Navigation zu: $route", e)
        CrashlyticsHelper.recordNavigationError(
            currentBackStackEntry?.destination?.route ?: "unknown",
            route,
            e.message ?: "Navigation failed"
        )
    }
}

/**
 * Navigiert zu einem Tab und verwirft dabei den DetailScreen.
 *
 * Verwendet für Tab-Navigation, um sicherzustellen, dass der DetailScreen geschlossen wird.
 * Unterscheidet zwischen Tab-Navigation und Detail-Navigation für optimale UX.
 *
 * Features:
 * - Intelligente BackStack-Verwaltung
 * - Automatische DetailScreen-Schließung bei Tab-Wechsel
 * - State-Restoration für Tab-Inhalte
 * - Optimierte Navigation-Performance
 * - Robuste Fehlerbehandlung
 * - Automatisches Logging
 *
 * @param route Die Ziel-Tab-Route für die Navigation
 */
fun NavController.navigateToTab(route: String) {
    try {
        // Prüfe ob wir aktuell auf dem DetailScreen sind
        val currentRoute = currentBackStackEntry?.destination?.route
        val isOnDetailScreen = currentRoute?.startsWith("detail/") == true

        this.navigate(route) {
            // Wenn wir auf einem DetailScreen sind, poppen wir bis zum Start-Tab
            if (isOnDetailScreen) {
                popUpTo(graph.startDestinationId) {
                    saveState = true
                    inclusive = false // Nicht inclusive, damit der Tab erhalten bleibt
                }
            } else {
                // Bei normaler Tab-Navigation nur State speichern
                popUpTo(graph.startDestinationId) {
                    saveState = true
                    inclusive = false
                }
            }
            launchSingleTop = true
            restoreState = true
        }
        AppLogger.d("Navigation", "Tab-Navigation zu: $route (von DetailScreen: $isOnDetailScreen)")
    } catch (e: Exception) {
        AppLogger.e("Navigation", "Fehler bei Tab-Navigation zu: $route", e)
        CrashlyticsHelper.recordNavigationError(
            currentBackStackEntry?.destination?.route ?: "unknown",
            route,
            e.message ?: "Tab navigation failed"
        )
    }
}

/**
 * Navigiert zu einem DetailScreen und ersetzt dabei alle anderen DetailScreens im BackStack.
 *
 * Verhindert das Problem, dass mehrere DetailScreens im BackStack bleiben und die falsche
 * Detailseite angezeigt wird. Stellt sicher, dass nur der aktuelle DetailScreen sichtbar ist.
 *
 * Features:
 * - Ersetzt alle anderen DetailScreens im BackStack
 * - Verhindert Duplikate von DetailScreens
 * - Optimierte Navigation-Performance
 * - Robuste Fehlerbehandlung
 * - Automatisches Logging
 *
 * @param gameId Die ID des Spiels für die Detailansicht
 */
fun NavController.navigateToDetail(gameId: Int) {
    try {
        val detailRoute = Routes.detail(gameId)

        // Prüfe, ob wir bereits auf dem gleichen DetailScreen sind
        val currentRoute = currentBackStackEntry?.destination?.route
        if (currentRoute == detailRoute) {
            AppLogger.d("Navigation", "Bereits auf DetailScreen für gameId: $gameId")
            return
        }

        this.navigate(detailRoute) {
            // Entferne alle anderen DetailScreens aus dem BackStack
            popUpTo(graph.startDestinationId) {
                saveState = true
                inclusive = false
            }
            // Stelle sicher, dass nur ein DetailScreen im BackStack ist
            launchSingleTop = true
            restoreState = false // Kein State-Restore für DetailScreens
        }

        AppLogger.d("Navigation", "Detail-Navigation zu: $detailRoute (gameId: $gameId)")
        AppAnalytics.trackEvent(
            "navigation_to_detail",
            mapOf("game_id" to gameId, "route" to detailRoute)
        )
    } catch (e: Exception) {
        AppLogger.e("Navigation", "Fehler bei Detail-Navigation zu gameId: $gameId", e)
        CrashlyticsHelper.recordNavigationError(
            currentBackStackEntry?.destination?.route ?: "unknown",
            "detail/$gameId",
            e.message ?: "Detail navigation failed"
        )
    }
}
