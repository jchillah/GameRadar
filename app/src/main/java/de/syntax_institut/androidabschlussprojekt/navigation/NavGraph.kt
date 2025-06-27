package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.syntax_institut.androidabschlussprojekt.ui.screens.DetailScreen
import de.syntax_institut.androidabschlussprojekt.ui.screens.SearchScreen
import de.syntax_institut.androidabschlussprojekt.navigation.Routes

/**
 * Navigation Graph.
 */
@Composable
fun NavGraph(navController: androidx.navigation.NavHostController) {
    NavHost(navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId") ?: return@composable
            DetailScreen(id, navController)
        }
    }
}