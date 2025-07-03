package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    setDarkTheme: (Boolean) -> Unit,
    cacheSize: Int,
    isOffline: Boolean,
    lastSyncTime: Long?,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH,
        modifier = modifier
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(navController)
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(modifier, navController)
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId") ?: return@composable
            DetailScreen(id, navController)
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
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                setDarkTheme = setDarkTheme,
                cacheSize = cacheSize,
                isOffline = isOffline,
                lastSyncTime = lastSyncTime
            )
        }
    }
}
