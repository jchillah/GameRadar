package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.navigation.Routes.STATS
import de.syntax_institut.androidabschlussprojekt.navigation.Routes.WISHLIST
import de.syntax_institut.androidabschlussprojekt.ui.screens.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Hauptnavigationsgraph der App mit allen Screen-Routen und Animationen.
 *
 * Definiert die Navigation zwischen allen Hauptbildschirmen:
 * - Search: Hauptsuchbildschirm
 * - Favorites: Favoritenliste
 * - Detail: Spieldetails mit gameId-Parameter
 * - Settings: Einstellungen mit Slide-Animationen
 * - Wishlist: Wunschliste
 * - Stats: Statistiken
 *
 * Features:
 * - Animierte Übergänge für Settings-Screen
 * - Parameter-basierte Navigation für Detail-Screen
 * - Logging für Navigation-Events
 * - Fehlerbehandlung für fehlende Parameter
 *
 * @param modifier Modifier für das Layout
 * @param navController Navigation Controller für die App
 */
@Composable
fun NavGraph(
        modifier: Modifier = Modifier,
        navController: NavHostController,
) {
        NavHost(
                navController = navController,
                startDestination = Routes.SEARCH,
                modifier = modifier
        ) {
                composable(route = Routes.SEARCH) {
                        PerformanceMonitor.incrementEventCounter("navigation_to_search")
                        SearchScreen(navController = navController)
                }

                composable(route = Routes.FAVORITES) {
                        PerformanceMonitor.incrementEventCounter("navigation_to_favorites")
                        FavoritesScreen(navController = navController)
                }

                composable(
                        route = Routes.DETAIL,
                        arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("gameId")
                        PerformanceMonitor.incrementEventCounter("navigation_to_detail")
                        PerformanceMonitor.trackNavigation(
                                "previous_screen",
                                "DetailScreen",
                                System.currentTimeMillis()
                        )

                        AppLogger.d(
                                "NavGraph",
                                "DetailScreen aufgerufen mit gameId=$id (BackStack: " +
                                        backStackEntry.arguments +
                                        ")"
                        )
                        if (id == null) return@composable
                        DetailScreen(gameId = id, navController = navController)
                }

                composable(
                        route = Routes.SETTINGS,
                        enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(700)
                                )
                        },
                        exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(700)
                                )
                        },
                        popEnterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(700)
                                )
                        },
                        popExitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(700)
                                )
                        }
                ) {
                        PerformanceMonitor.incrementEventCounter("navigation_to_settings")
                        SettingsScreen()
                }

                composable(WISHLIST) {
                        PerformanceMonitor.incrementEventCounter("navigation_to_wishlist")
                        WishlistScreen(navController = navController)
                }
                composable(STATS) {
                        PerformanceMonitor.incrementEventCounter("navigation_to_stats")
                        StatsScreen(navController = navController)
                }
        }
}
