package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Column(
        modifier = modifier.fillMaxWidth(),
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
                if (searchText.text.isNotBlank()) viewModel.search(searchText.text.trim())
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
                        if (!isOnline) stringResource(R.string.search_empty_message_offline)
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
                        // Toggle-Logik: Wenn der neue Status true ist, hinzufügen, sonst
                        // entfernen
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
                onOrderingChange = { newOrdering -> viewModel.updateOrdering(newOrdering) },
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

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController())
}
