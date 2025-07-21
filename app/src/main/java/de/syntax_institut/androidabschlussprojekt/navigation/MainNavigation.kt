package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Root-Navigation der App mit BottomBar und NavGraph-Integration.
 *
 * Features:
 * - Zentrale Navigation-Struktur der App
 * - Bottom-Navigation-Bar für Hauptbildschirme
 * - NavGraph für Screen-Navigation
 * - Automatische Route-Erkennung für BottomBar-Highlighting
 * - Material3 Scaffold-Layout
 *
 * Struktur:
 * - BottomNavBar: Navigation zwischen Hauptbildschirmen
 * - NavGraph: Screen-spezifische Navigation mit Parametern
 * - Responsive Layout mit Padding-Anpassung
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Logging für Navigation-Debugging
    LaunchedEffect(currentRoute) { AppLogger.d("MainNavigation", "Aktuelle Route: $currentRoute") }

    Scaffold(
        modifier = Modifier.padding(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar(currentRoute, navController) }
    ) { innerPadding ->
        NavGraph(modifier = Modifier.padding(innerPadding), navController = navController)
    }
}
