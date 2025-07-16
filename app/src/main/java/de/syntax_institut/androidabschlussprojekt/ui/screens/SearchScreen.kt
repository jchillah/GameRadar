package de.syntax_institut.androidabschlussprojekt.ui.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    var selectedTab by remember { mutableIntStateOf(0) }
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isProUser by settingsViewModel.proStatus.collectAsState()
    val analyticsEnabled by settingsViewModel.analyticsEnabled.collectAsState()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val favoritesState by favoritesViewModel.uiState.collectAsState()
    val favoriteIds =
        remember(favoritesState.favorites) { favoritesState.favorites.map { it.id }.toSet() }

    // WishlistViewModel einbinden
    val wishlistViewModel: WishlistViewModel = koinViewModel()
    val wishlistGames by wishlistViewModel.wishlistGames.collectAsState()
    val wishlistIds = remember(wishlistGames) { wishlistGames.map { it.id }.toSet() }

    LaunchedEffect(Unit) {
        if (state.platforms.isEmpty()) viewModel.loadPlatforms()
        if (state.genres.isEmpty()) viewModel.loadGenres()
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
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_button),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FilterBottomSheet(
                        platforms = state.platforms,
                        genres = state.genres,
                        selectedPlatforms = state.selectedPlatforms,
                        selectedGenres = state.selectedGenres,
                        rating = state.rating,
                        ordering = state.ordering,
                        isLoadingPlatforms = state.isLoadingPlatforms,
                        isLoadingGenres = state.isLoadingGenres,
                        platformsErrorId = state.platformsErrorId,
                        genresErrorId = state.genresErrorId,
                        isOffline = !isOnline,
                        onOrderingChange = { newOrdering ->
                            viewModel.updateOrdering(newOrdering)
                        },
                        onFilterChange = { newPlatforms, newGenres, newRating ->
                            viewModel.updateFilters(newPlatforms, newGenres, newRating)
                            if (searchText.text.isNotBlank()) {
                                viewModel.search(searchText.text.trim())
                            }
                        },
                        onRetryPlatforms = { viewModel.loadPlatforms() },
                        onRetryGenres = { viewModel.loadGenres() },
                        onClearCache = { viewModel.clearCache() }
                    )
                }
                // Hauptinhalt (rechts)
                Column(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 8.dp)) {
                    SearchHeader(onFilterClick = {}) // Button ist auf LargeScreen deaktiviert
                    SearchTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { index ->
                            selectedTab = index
                            when (index) {
                                0 -> viewModel.updateOrdering("")
                                1 -> viewModel.updateOrdering("-released")
                                2 -> viewModel.updateOrdering("-rating")
                            }
                            viewModel.search(searchText.text.trim())
                        }
                    )
                    SearchBarWithButton(
                        searchText = searchText,
                        onTextChange = {
                            searchText = it
                            if (it.text.isBlank()) viewModel.resetSearch()
                        },
                        onSearchClick = {
                            if (searchText.text.isNotBlank())
                                viewModel.search(searchText.text.trim())
                        },
                        isLoading = state.isLoading,
                        onClear = {
                            searchText = TextFieldValue("")
                            viewModel.resetSearch()
                        }
                    )
                    ActiveFiltersRow(
                        selectedPlatformIds = state.selectedPlatforms,
                        selectedGenreIds = state.selectedGenres,
                        allPlatforms = state.platforms,
                        allGenres = state.genres,
                        rating = state.rating,
                        ordering = state.ordering,
                        onRemovePlatform = { id -> viewModel.removePlatformFilter(id) },
                        onRemoveGenre = { id -> viewModel.removeGenreFilter(id) },
                        onRemoveRating = { viewModel.removeRatingFilter() },
                        onRemoveOrdering = { viewModel.removeOrderingFilter() },
                        onClearAll = { viewModel.clearAllFilters() }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.errorMessageId != null) {
                            ErrorCard(
                                modifier = Modifier.fillMaxSize(),
                                error = stringResource(state.errorMessageId!!),
                            )
                        } else if (!state.hasSearched) {
                            EmptyState(
                                title = stringResource(R.string.search_empty_title),
                                message =
                                    if (!isOnline)
                                        stringResource(
                                            R.string.search_empty_message_offline
                                        )
                                    else
                                        stringResource(
                                            R.string.search_empty_message_online
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
                                    navController.navigateSingleTopTo(Routes.detail(game.id))
                                },
                                modifier = Modifier.fillMaxSize(),
                                imageQuality = imageQuality,
                                favoriteIds = favoriteIds,
                                wishlistIds = wishlistIds,
                                onWishlistChanged = { game, isInWishlist ->
                                    if (isInWishlist) {
                                        wishlistViewModel.addToWishlist(game)
                                    } else {
                                        wishlistViewModel.removeFromWishlist(game.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            // Banner am unteren Rand für große Bildschirme
            if (!isProUser) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    BannerAdView(
                        adUnitId = "ca-app-pub-7269049262039376/9765911397",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 8.dp),
                        analyticsEnabled = analyticsEnabled
                    )
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
                        when (index) {
                            0 -> viewModel.updateOrdering("")
                            1 -> viewModel.updateOrdering("-released")
                            2 -> viewModel.updateOrdering("-rating")
                        }
                        viewModel.search(searchText.text.trim())
                    }
                )
                SearchBarWithButton(
                    searchText = searchText,
                    onTextChange = {
                        searchText = it
                        if (it.text.isBlank()) viewModel.resetSearch()
                    },
                    onSearchClick = {
                        if (searchText.text.isNotBlank())
                            viewModel.search(searchText.text.trim())
                    },
                    isLoading = state.isLoading,
                    onClear = {
                        searchText = TextFieldValue("")
                        viewModel.resetSearch()
                    }
                )
                ActiveFiltersRow(
                    selectedPlatformIds = state.selectedPlatforms,
                    selectedGenreIds = state.selectedGenres,
                    allPlatforms = state.platforms,
                    allGenres = state.genres,
                    rating = state.rating,
                    ordering = state.ordering,
                    onRemovePlatform = { id -> viewModel.removePlatformFilter(id) },
                    onRemoveGenre = { id -> viewModel.removeGenreFilter(id) },
                    onRemoveRating = { viewModel.removeRatingFilter() },
                    onRemoveOrdering = { viewModel.removeOrderingFilter() },
                    onClearAll = { viewModel.clearAllFilters() }
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (state.errorMessageId != null) {
                        ErrorCard(
                            modifier = Modifier.fillMaxSize(),
                            error = stringResource(state.errorMessageId!!),
                        )
                    } else if (!state.hasSearched) {
                        EmptyState(
                            title = stringResource(R.string.search_empty_title),
                            message =
                                if (!isOnline)
                                    stringResource(
                                        R.string.search_empty_message_offline
                                    )
                                else stringResource(R.string.search_empty_message_online),
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
                                navController.navigateSingleTopTo(Routes.detail(game.id))
                            },
                            modifier = Modifier.fillMaxSize(),
                            imageQuality = imageQuality,
                            favoriteIds = favoriteIds,
                            wishlistIds = wishlistIds,
                            onWishlistChanged = { game, isInWishlist ->
                                if (isInWishlist) {
                                    wishlistViewModel.addToWishlist(game)
                                } else {
                                    wishlistViewModel.removeFromWishlist(game.id)
                                }
                            }
                        )
                    }
                }
            }
            // Banner am unteren Rand für kleine Bildschirme
            if (!isProUser) {
                BannerAdView(
                    adUnitId = "ca-app-pub-7269049262039376/9765911397",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 8.dp),
                    analyticsEnabled = analyticsEnabled
                )
            }
            if (showFilters) {
                ModalBottomSheet(onDismissRequest = { showFilters = false }) {
                    FilterBottomSheet(
                        platforms = state.platforms,
                        genres = state.genres,
                        selectedPlatforms = state.selectedPlatforms,
                        selectedGenres = state.selectedGenres,
                        rating = state.rating,
                        ordering = state.ordering,
                        isLoadingPlatforms = state.isLoadingPlatforms,
                        isLoadingGenres = state.isLoadingGenres,
                        platformsErrorId = state.platformsErrorId,
                        genresErrorId = state.genresErrorId,
                        isOffline = !isOnline,
                        onOrderingChange = { newOrdering ->
                            viewModel.updateOrdering(newOrdering)
                        },
                        onFilterChange = { newPlatforms, newGenres, newRating ->
                            viewModel.updateFilters(newPlatforms, newGenres, newRating)
                            if (searchText.text.isNotBlank()) {
                                viewModel.search(searchText.text.trim())
                            }
                            showFilters = false
                        },
                        onRetryPlatforms = { viewModel.loadPlatforms() },
                        onRetryGenres = { viewModel.loadGenres() },
                        onClearCache = { viewModel.clearCache() }
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
