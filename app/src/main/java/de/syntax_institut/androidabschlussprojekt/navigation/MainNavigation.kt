package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*

/**
 * Root-Navigation der App – enthält BottomBar und NavGraph
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.padding(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(currentRoute, navController)
        }
    ) { innerPadding ->
        // NavGraph ohne modifier, damit die Screens den gesamten Platz ausfüllen
        // Das Padding wird von den einzelnen Screens selbst gehandhabt
        NavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}
