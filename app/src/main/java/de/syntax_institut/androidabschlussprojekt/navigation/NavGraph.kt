package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Navigation Graph.
 * Zentrale Steuerung der Navigation für die gesamte App.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // ViewModels für Badge-Daten
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    
    // State für Badges
    val favoritesState by favoritesViewModel.uiState.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()
    val pagingItems = searchViewModel.pagingFlow.collectAsLazyPagingItems()
    
    // Anzahl der Favoriten
    val favoritesCount = favoritesState.favorites.size
    
    // Anzahl der Suchergebnisse (nur wenn gesucht wurde)
    val searchResultsCount = if (searchState.hasSearched) {
        pagingItems.itemCount
    } else {
        0
    }
    
    // Aktuelle Route für Tab-Auswahl
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { 
                        Box {
                            Icon(Icons.Filled.Search, contentDescription = "Suche")
                            if (searchResultsCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = if (searchResultsCount > 99) "99+" else searchResultsCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    },
                    label = { Text("Suche") },
                    selected = currentRoute == Routes.SEARCH,
                    onClick = { 
                        selectedTab = 0
                        navController.navigate(Routes.SEARCH) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { 
                        Box {
                            Icon(Icons.Filled.Favorite, contentDescription = "Favoriten")
                            if (favoritesCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = if (favoritesCount > 99) "99+" else favoritesCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    },
                    label = { Text("Favoriten") },
                    selected = currentRoute == Routes.FAVORITES,
                    onClick = { 
                        selectedTab = 1
                        navController.navigate(Routes.FAVORITES) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SEARCH,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Search Screen
            composable(Routes.SEARCH) {
                SearchScreen(navController, modifier = Modifier)
            }
            
            // Favorites Screen
            composable(Routes.FAVORITES) {
                FavoritesScreen(navController, modifier = Modifier)
            }
            
            // Detail Screen (außerhalb der Bottom Navigation)
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("gameId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("gameId") ?: return@composable
                DetailScreen(id, navController)
            }
        }
    }
}