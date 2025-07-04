package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Die untere Navigationsleiste mit Badges für Ergebnisse und Favoriten
 */
@Composable
fun BottomNavBar(
    currentRoute: String?,
    navController: NavHostController,
) {
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()

    val favoritesState by favoritesViewModel.uiState.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()
    val lazyPagingItems = searchViewModel.pagingFlow.collectAsLazyPagingItems()

    val favoritesCount = favoritesState.favorites.size
    val searchResultsCount = if (searchState.hasSearched) lazyPagingItems.itemCount else 0

    NavigationBar {
        NavigationBarItem(
            icon = {
                Box {
                    Icon(Icons.Filled.Search, contentDescription = "Suche")
                    if (searchResultsCount > 0) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(if (searchResultsCount > 99) "99+" else searchResultsCount.toString())
                        }
                    }
                }
            },
            label = { Text("Suche") },
            selected = currentRoute == Routes.SEARCH,
            onClick = {
                navController.navigateToTab(Routes.SEARCH)
            }
        )

        NavigationBarItem(
            icon = {
                Box {
                    Icon(Icons.Filled.Favorite, contentDescription = "Favoriten")
                    if (favoritesCount > 0) {
                        Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                            Text(if (favoritesCount > 99) "99+" else favoritesCount.toString())
                        }
                    }
                }
            },
            label = { Text("Favoriten") },
            selected = currentRoute == Routes.FAVORITES,
            onClick = {
                navController.navigateToTab(Routes.FAVORITES)
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Einstellungen") },
            label = { Text("Einstellungen") },
            selected = currentRoute == Routes.SETTINGS,
            onClick = {
                navController.navigateToTab(Routes.SETTINGS)
            }
        )
    }
}
