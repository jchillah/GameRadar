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
    modifier: Modifier,
    isDarkTheme: Boolean = false,
    setDarkTheme: (Boolean) -> Unit = {},
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val searchViewModel: de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel =
        org.koin.androidx.compose.koinViewModel()
    val cacheSize by searchViewModel.cacheSize.collectAsState()
    val isOffline by searchViewModel.isOffline.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()
    val lastSyncTime = searchState.lastSyncTime

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(currentRoute, navController)
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            isDarkTheme = isDarkTheme,
            setDarkTheme = setDarkTheme,
            cacheSize = cacheSize,
            isOffline = isOffline,
            lastSyncTime = lastSyncTime,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
