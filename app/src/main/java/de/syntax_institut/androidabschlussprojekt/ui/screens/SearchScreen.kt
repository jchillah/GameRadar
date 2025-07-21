package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.androidx.compose.*

/**
 * Hauptbildschirm für die Spielesuche mit erweiterten Filtern und responsivem Layout.
 *
 * Features:
 * - Responsive Design: Sidebar-Layout für große Bildschirme, BottomSheet für kleine
 * - Erweiterte Filter: Plattformen, Genres, Bewertung, Sortierung
 * - Tab-Navigation: Alle, Neu veröffentlicht, Top bewertet
 * - Paging für große Ergebnislisten
 * - Offline-Unterstützung mit Cache
 * - Integration mit Favoriten und Wunschliste
 * - Analytics-Tracking
 *
 * @param modifier Modifier für das Layout
 * @param navController Navigation Controller für Screen-Navigation
 * @param searchViewModel ViewModel für die Suchlogik
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    val searchState by searchViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    val pagingItems = searchViewModel.pagingFlow.collectAsLazyPagingItems()
    var selectedTab by remember { mutableIntStateOf(0) }
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val imageQuality = settingsState.imageQuality
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val favoritesState by favoritesViewModel.uiState.collectAsState()
    val favoriteIds =
        remember(favoritesState.favorites) {
            favoritesState.favorites.map { it.id }.toSet()
        }

    // WishlistViewModel einbinden
    val wishlistViewModel: WishlistViewModel = koinViewModel()
    val wishlistGames by wishlistViewModel.wishlistGames.collectAsState()
    val wishlistIds = remember(wishlistGames) { wishlistGames.map { it.id }.toSet() }

    LaunchedEffect(Unit) {
        AppAnalytics.trackScreenView("SearchScreen")
        // Lade Plattformen und Genres nur wenn sie leer sind
        if (searchState.platforms.isEmpty()) {
            searchViewModel.loadPlatforms()
        }
        if (searchState.genres.isEmpty()) {
            searchViewModel.loadGenres()
        }
    }

    // Stelle sicher, dass der SearchState korrekt wiederhergestellt wird
    LaunchedEffect(searchState) {
        // Logging für Debugging
        AppLogger.d(
            "SearchScreen",
            "SearchState updated: hasSearched=${searchState.hasSearched}, platforms=${searchState.platforms.size}, genres=${searchState.genres.size}"
        )
    }

    val minTabletWidth: Dp = 840.dp

    BoxWithConstraints(modifier = modifier) {
        val isLargeScreen = maxWidth >= minTabletWidth
        if (isLargeScreen) {
            Row(Modifier.fillMaxSize()) {
                // Filter-Sidebar (links)
                Column(
                    modifier =
                        Modifier
                            .widthIn(min = 260.dp, max = 340.dp)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .background(
                                MaterialTheme.colorScheme
                                    .surfaceVariant
                            )
                            .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_button),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FilterBottomSheet(
                        platforms = searchState.platforms,
                        genres = searchState.genres,
                        selectedPlatforms = searchState.selectedPlatforms,
                        selectedGenres = searchState.selectedGenres,
                        rating = searchState.rating,
                        ordering = searchState.ordering,
                        isLoadingPlatforms = searchState.isLoadingPlatforms,
                        isLoadingGenres = searchState.isLoadingGenres,
                        platformsErrorId = searchState.platformsErrorId,
                        genresErrorId = searchState.genresErrorId,
                        isOffline = !isOnline,
                        onOrderingChange = { newOrdering ->
                            searchViewModel.updateOrdering(newOrdering)
                        },
                        onFilterChange = {
                                newPlatforms,
                                newGenres,
                                newRating,
                            ->
                            searchViewModel.updateFilters(
                                newPlatforms,
                                newGenres,
                                newRating
                            )
                            AppAnalytics.trackEvent(
                                "filters_applied",
                                mapOf(
                                    "platforms_count" to
                                            newPlatforms.size,
                                    "genres_count" to
                                            newGenres.size,
                                    "rating" to newRating
                                )
                            )
                            if (searchText.text.isNotBlank()) {
                                searchViewModel.search(
                                    searchText.text.trim()
                                )
                            }
                        },
                        onRetryPlatforms = {
                            searchViewModel.loadPlatforms()
                        },
                        onRetryGenres = { searchViewModel.loadGenres() },
                        onClearCache = { searchViewModel.clearCache() }
                    )
                }
                // Hauptinhalt (rechts)
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 8.dp)
                ) {
                    SearchHeader(
                        onFilterClick = {}
                    ) // Button ist auf LargeScreen deaktiviert
                    SearchTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { index ->
                            selectedTab = index
                            val ordering =
                                when (index) {
                                    0 -> ""
                                    1 -> "-released"
                                    2 -> "-rating"
                                    else -> ""
                                }
                            searchViewModel.updateOrdering(ordering)
                            AppAnalytics.trackEvent(
                                "search_tab_changed",
                                mapOf(
                                    "tab_index" to index,
                                    "ordering" to ordering
                                )
                            )
                            searchViewModel.search(
                                searchText.text.trim()
                            )
                        }
                    )
                    SearchBarWithButton(
                        searchText = searchText,
                        onTextChange = {
                            searchText = it
                            if (it.text.isBlank())
                                searchViewModel.resetSearch()
                        },
                        onSearchClick = {
                            if (searchText.text.isNotBlank()) {
                                val query = searchText.text.trim()
                                searchViewModel.search(query)
                                AppAnalytics.trackEvent(
                                    "search_performed",
                                    mapOf(
                                        "query" to query,
                                        "has_filters" to
                                                (searchState
                                                    .selectedPlatforms
                                                    .isNotEmpty() ||
                                                        searchState
                                                            .selectedGenres
                                                            .isNotEmpty() ||
                                                        searchState
                                                            .rating >
                                                        0)
                                    )
                                )
                            }
                        },
                        isLoading = searchState.isLoading,
                        onClear = {
                            searchText = TextFieldValue("")
                            searchViewModel.resetSearch()
                        }
                    )
                    ActiveFiltersRow(
                        selectedPlatformIds = searchState.selectedPlatforms,
                        selectedGenreIds = searchState.selectedGenres,
                        allPlatforms = searchState.platforms,
                        allGenres = searchState.genres,
                        rating = searchState.rating,
                        ordering = searchState.ordering,
                        onRemovePlatform = { id ->
                            searchViewModel.removePlatformFilter(id)
                        },
                        onRemoveGenre = { id ->
                            searchViewModel.removeGenreFilter(id)
                        },
                        onRemoveRating = {
                            searchViewModel.removeRatingFilter()
                        },
                        onRemoveOrdering = {
                            searchViewModel.removeOrderingFilter()
                        },
                        onClearAll = { searchViewModel.clearAllFilters() }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchState.errorMessageId != null) {
                            ErrorCard(
                                modifier = Modifier.fillMaxSize(),
                                error =
                                    stringResource(
                                        searchState
                                            .errorMessageId!!
                                    ),
                            )
                        } else if (!searchState.hasSearched) {
                            EmptyState(
                                title =
                                    stringResource(
                                        R.string
                                            .search_empty_title
                                    ),
                                message =
                                    if (!isOnline)
                                        stringResource(
                                            R.string
                                                .search_empty_message_offline
                                        )
                                    else
                                        stringResource(
                                            R.string
                                                .search_empty_message_online
                                        ),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            SearchResultContent(
                                pagingItems = pagingItems,
                                onGameClick = { game ->
                                    AppLogger.d(
                                        "Navigation",
                                        "Navigiere zu DetailScreen mit gameId=${game.id}"
                                    )
                                    AppAnalytics.trackEvent(
                                        "game_selected",
                                        mapOf(
                                            "game_id" to
                                                    game.id,
                                            "game_title" to
                                                    game.title,
                                            "source" to
                                                    "search_screen"
                                        )
                                    )
                                    navController
                                        .navigateToDetail(
                                            game.id
                                        )
                                },
                                modifier = Modifier.fillMaxSize(),
                                imageQuality = imageQuality,
                                favoriteIds = favoriteIds,
                                wishlistIds = wishlistIds,
                                onWishlistChanged = {
                                        game,
                                        isInWishlist,
                                    ->
                                    if (isInWishlist) {
                                        wishlistViewModel
                                            .addToWishlist(
                                                game
                                            )
                                        AppAnalytics
                                            .trackEvent(
                                                "wishlist_added",
                                                mapOf(
                                                    "game_id" to
                                                            game.id,
                                                    "game_title" to
                                                            game.title,
                                                    "source" to
                                                            "search_screen"
                                                )
                                            )
                                    } else {
                                        wishlistViewModel
                                            .removeFromWishlist(
                                                game.id
                                            )
                                        AppAnalytics
                                            .trackEvent(
                                                "wishlist_removed",
                                                mapOf(
                                                    "game_id" to
                                                            game.id,
                                                    "game_title" to
                                                            game.title,
                                                    "source" to
                                                            "search_screen"
                                                )
                                            )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // Bisheriges Layout für kleine Bildschirme
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SearchHeader(onFilterClick = { showFilters = true })
                SearchTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        val ordering =
                            when (index) {
                                0 -> ""
                                1 -> "-released"
                                2 -> "-rating"
                                else -> ""
                            }
                        searchViewModel.updateOrdering(ordering)
                        AppAnalytics.trackEvent(
                            "search_tab_changed",
                            mapOf(
                                "tab_index" to index,
                                "ordering" to ordering
                            )
                        )
                        searchViewModel.search(searchText.text.trim())
                    }
                )
                SearchBarWithButton(
                    searchText = searchText,
                    onTextChange = {
                        searchText = it
                        if (it.text.isBlank()) searchViewModel.resetSearch()
                    },
                    onSearchClick = {
                        if (searchText.text.isNotBlank()) {
                            val query = searchText.text.trim()
                            searchViewModel.search(query)
                            AppAnalytics.trackEvent(
                                "search_performed",
                                mapOf(
                                    "query" to query,
                                    "has_filters" to
                                            (searchState
                                                .selectedPlatforms
                                                .isNotEmpty() ||
                                                    searchState
                                                        .selectedGenres
                                                        .isNotEmpty() ||
                                                    searchState
                                                        .rating >
                                                    0)
                                )
                            )
                        }
                    },
                    isLoading = searchState.isLoading,
                    onClear = {
                        searchText = TextFieldValue("")
                        searchViewModel.resetSearch()
                    }
                )
                ActiveFiltersRow(
                    selectedPlatformIds = searchState.selectedPlatforms,
                    selectedGenreIds = searchState.selectedGenres,
                    allPlatforms = searchState.platforms,
                    allGenres = searchState.genres,
                    rating = searchState.rating,
                    ordering = searchState.ordering,
                    onRemovePlatform = { id ->
                        searchViewModel.removePlatformFilter(id)
                    },
                    onRemoveGenre = { id ->
                        searchViewModel.removeGenreFilter(id)
                    },
                    onRemoveRating = { searchViewModel.removeRatingFilter() },
                    onRemoveOrdering = {
                        searchViewModel.removeOrderingFilter()
                    },
                    onClearAll = { searchViewModel.clearAllFilters() }
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (searchState.errorMessageId != null) {
                        ErrorCard(
                            modifier = Modifier.fillMaxSize(),
                            error =
                                stringResource(
                                    searchState.errorMessageId!!
                                ),
                        )
                    } else if (!searchState.hasSearched) {
                        EmptyState(
                            title =
                                stringResource(
                                    R.string.search_empty_title
                                ),
                            message =
                                if (!isOnline)
                                    stringResource(
                                        R.string
                                            .search_empty_message_offline
                                    )
                                else
                                    stringResource(
                                        R.string
                                            .search_empty_message_online
                                    ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        SearchResultContent(
                            pagingItems = pagingItems,
                            onGameClick = { game ->
                                AppLogger.d(
                                    "Navigation",
                                    "Navigiere zu DetailScreen mit gameId=${game.id}"
                                )
                                AppAnalytics.trackEvent(
                                    "game_selected",
                                    mapOf(
                                        "game_id" to
                                                game.id,
                                        "game_title" to
                                                game.title,
                                        "source" to
                                                "search_screen"
                                    )
                                )
                                navController.navigateToDetail(
                                    game.id
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            imageQuality = imageQuality,
                            favoriteIds = favoriteIds,
                            wishlistIds = wishlistIds,
                            onWishlistChanged = { game, isInWishlist ->
                                if (isInWishlist) {
                                    wishlistViewModel
                                        .addToWishlist(game)
                                    AppAnalytics.trackEvent(
                                        "wishlist_added",
                                        mapOf(
                                            "game_id" to
                                                    game.id,
                                            "game_title" to
                                                    game.title,
                                            "source" to
                                                    "search_screen"
                                        )
                                    )
                                } else {
                                    wishlistViewModel
                                        .removeFromWishlist(
                                            game.id
                                        )
                                    AppAnalytics.trackEvent(
                                        "wishlist_removed",
                                        mapOf(
                                            "game_id" to
                                                    game.id,
                                            "game_title" to
                                                    game.title,
                                            "source" to
                                                    "search_screen"
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
            if (showFilters) {
                ModalBottomSheet(onDismissRequest = { showFilters = false }) {
                    FilterBottomSheet(
                        platforms = searchState.platforms,
                        genres = searchState.genres,
                        selectedPlatforms = searchState.selectedPlatforms,
                        selectedGenres = searchState.selectedGenres,
                        rating = searchState.rating,
                        ordering = searchState.ordering,
                        isLoadingPlatforms = searchState.isLoadingPlatforms,
                        isLoadingGenres = searchState.isLoadingGenres,
                        platformsErrorId = searchState.platformsErrorId,
                        genresErrorId = searchState.genresErrorId,
                        isOffline = !isOnline,
                        onOrderingChange = { newOrdering ->
                            searchViewModel.updateOrdering(newOrdering)
                        },
                        onFilterChange = {
                                newPlatforms,
                                newGenres,
                                newRating,
                            ->
                            searchViewModel.updateFilters(
                                newPlatforms,
                                newGenres,
                                newRating
                            )
                            AppAnalytics.trackEvent(
                                "filters_applied",
                                mapOf(
                                    "platforms_count" to
                                            newPlatforms.size,
                                    "genres_count" to
                                            newGenres.size,
                                    "rating" to newRating
                                )
                            )
                            if (searchText.text.isNotBlank()) {
                                searchViewModel.search(
                                    searchText.text.trim()
                                )
                            }
                            showFilters = false
                        },
                        onRetryPlatforms = {
                            searchViewModel.loadPlatforms()
                        },
                        onRetryGenres = { searchViewModel.loadGenres() },
                        onClearCache = { searchViewModel.clearCache() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController())
}
