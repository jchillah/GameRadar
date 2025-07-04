package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Root-Navigation der App – enthält BottomBar und NavGraph
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    modifier: Modifier,
    isDarkTheme: Boolean = false,
    setDarkTheme: (Boolean) -> Unit = {},
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // ViewModel sicher erstellen
    val searchViewModel = koinViewModel<SearchViewModel>()

    // States sicher sammeln mit Fallback-Werten
    val isOffline by searchViewModel.isOffline.collectAsState(initial = false)
    val searchState by searchViewModel.uiState.collectAsState(initial = null)
    val lastSyncTime = searchState?.lastSyncTime ?: 0L

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(currentRoute, navController)
        }
    ) { innerPadding ->
        // NavGraph ohne modifier, damit die Screens den gesamten Platz ausfüllen
        // Das Padding wird von den einzelnen Screens selbst gehandhabt
        NavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            isDarkTheme = isDarkTheme,
            setDarkTheme = setDarkTheme,
            isOffline = isOffline,
            lastSyncTime = lastSyncTime
        )
    }
}
