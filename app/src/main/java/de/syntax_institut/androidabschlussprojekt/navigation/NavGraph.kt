package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.navigation.Routes.WISHLIST
import de.syntax_institut.androidabschlussprojekt.ui.screens.*
import de.syntax_institut.androidabschlussprojekt.utils.*

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = Routes.SEARCH, modifier = modifier) {
        composable(route = Routes.SEARCH) { SearchScreen(navController = navController) }

        composable(route = Routes.FAVORITES) { FavoritesScreen(navController = navController) }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId")
            AppLogger.d("NavGraph", "DetailScreen aufgerufen mit gameId=$id")
            if (id == null) return@composable
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
        ) { SettingsScreen() }

        composable(WISHLIST) { WishlistScreen(navController = navController) }
    }
}
