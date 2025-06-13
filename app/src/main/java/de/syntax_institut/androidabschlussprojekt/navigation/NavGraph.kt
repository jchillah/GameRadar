package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            HomeScreen(onNavigateToSettings = {
                navController.navigate(Routes.Settings.route)
            })
        }
        composable(Routes.Settings.route) {
            SettingsScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}
