package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToSettings = {
                navController.navigate(Screen.Settings.route)
            })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}
