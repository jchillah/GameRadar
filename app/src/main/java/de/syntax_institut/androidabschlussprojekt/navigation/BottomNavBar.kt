package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.navigation.Routes.WISHLIST
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Die untere Navigationsleiste mit Badges für Ergebnisse und Favoriten.
 *
 * Features:
 * - Material3 NavigationBar mit vier Hauptbildschirmen
 * - Dynamische Badges für Suchergebnisse, Favoriten und Wunschliste
 * - Badge-Limitierung auf "99+" für große Zahlen
 * - Automatische Route-Erkennung für Highlighting
 * - Tab-Navigation mit State-Restoration
 * - ViewModel-Integration für Live-Daten
 *
 * Navigation-Items:
 * - Search: Suchergebnisse mit Badge
 * - Favorites: Favoritenanzahl mit Badge
 * - Wishlist: Wunschlistenanzahl mit Badge
 * - Settings: Einstellungen ohne Badge
 *
 * @param currentRoute Aktuelle Route für Highlighting
 * @param navController Navigation Controller für Tab-Navigation
 */
@Composable
fun BottomNavBar(
    currentRoute: String?,
    navController: NavHostController,
) {
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val wishlistViewModel: WishlistViewModel = koinViewModel()

    val favoritesState by favoritesViewModel.uiState.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()
    val lazyPagingItems = searchViewModel.pagingFlow.collectAsLazyPagingItems()
    val wishlist by wishlistViewModel.wishlistGames.collectAsState()

    val favoritesCount = favoritesState.favorites.size
    val searchResultsCount = if (searchState.hasSearched) lazyPagingItems.itemCount else 0
    val wishlistCount = wishlist.size

    NavigationBar(
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Box {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = stringResource(R.string.nav_search),
                        modifier = Modifier.size(24.dp)
                    )
                    if (searchResultsCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp),
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Text(
                                if (searchResultsCount > 99) "99+"
                                else searchResultsCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            },
            label = {
                Text(
                    stringResource(R.string.nav_search),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = currentRoute == Routes.SEARCH,
            onClick = { navController.navigateToTab(Routes.SEARCH) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            icon = {
                Box {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.nav_favorites),
                        modifier = Modifier.size(24.dp)
                    )
                    if (favoritesCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp),
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                if (favoritesCount > 99) "99+" else favoritesCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            },
            label = {
                Text(
                    stringResource(R.string.nav_favorites),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = currentRoute == Routes.FAVORITES,
            onClick = { navController.navigateToTab(Routes.FAVORITES) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            icon = {
                Box {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = stringResource(R.string.wishlist_tab),
                        modifier = Modifier.size(24.dp)
                    )
                    if (wishlistCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp),
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                if (wishlistCount > 99) "99+" else wishlistCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            },
            label = {
                Text(
                    stringResource(R.string.wishlist_tab),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = currentRoute == WISHLIST,
            onClick = { navController.navigateToTab(WISHLIST) },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.nav_settings),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    stringResource(R.string.nav_settings),
                    style = MaterialTheme.typography.labelMedium
                ) 
            },
            selected = currentRoute == Routes.SETTINGS,
            onClick = { navController.navigateToTab(Routes.SETTINGS) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
