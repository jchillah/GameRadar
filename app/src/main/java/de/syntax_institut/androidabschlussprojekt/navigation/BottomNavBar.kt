package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.R
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
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = stringResource(R.string.nav_search)
                    )
                    if (searchResultsCount > 0) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(if (searchResultsCount > 99) "99+" else searchResultsCount.toString())
                        }
                    }
                }
            },
            label = { Text(stringResource(R.string.nav_search)) },
            selected = currentRoute == Routes.SEARCH,
            onClick = {
                navController.navigateSingleTopTo(Routes.SEARCH)
            }
        )

        NavigationBarItem(
            icon = {
                Box {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.nav_favorites)
                    )
                    if (favoritesCount > 0) {
                        Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                            Text(if (favoritesCount > 99) "99+" else favoritesCount.toString())
                        }
                    }
                }
            },
            label = { Text(stringResource(R.string.nav_favorites)) },
            selected = currentRoute == Routes.FAVORITES,
            onClick = {
                navController.navigateSingleTopTo(Routes.FAVORITES)
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.nav_settings)
                )
            },
            label = { Text(stringResource(R.string.nav_settings)) },
            selected = currentRoute == Routes.SETTINGS,
            onClick = {
                navController.navigateSingleTopTo(Routes.SETTINGS)
            }
        )
    }
}
