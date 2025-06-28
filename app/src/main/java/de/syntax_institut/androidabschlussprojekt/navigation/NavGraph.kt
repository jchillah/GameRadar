package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.syntax_institut.androidabschlussprojekt.ui.screens.DetailScreen
import de.syntax_institut.androidabschlussprojekt.ui.screens.MainScreen

/**
 * Navigation Graph.
 * Zentrale Steuerung der Navigation für die gesamte App.
 */
@Composable
fun NavGraph(navController: androidx.navigation.NavHostController) {
    NavHost(navController, startDestination = Routes.HOME) {
        // Main Screen als Container für Bottom Navigation
        composable(Routes.HOME) { 
            MainScreen(navController = navController) 
        }
        
        // Detail Screen (außerhalb der MainScreen-Navigation)
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId") ?: return@composable
            DetailScreen(id, navController)
        }
    }
}