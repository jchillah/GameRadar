package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
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
    val isOffline by viewModel.isOffline.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val tabTitles = listOf("Alle", "Neuerscheinungen", "Top-rated")
    var selectedTab by remember { mutableIntStateOf(0) }
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()
    LaunchedEffect(Unit) {
        if (state.platforms.isEmpty()) viewModel.loadPlatforms()
        if (state.genres.isEmpty()) viewModel.loadGenres()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                maxLines = 1,
                text = "Spielsuche",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { showFilters = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter anzeigen")
            }
        }
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        when (index) {
                            0 -> viewModel.updateOrdering("")
                            1 -> viewModel.updateOrdering("-released")
                            2 -> viewModel.updateOrdering("-rating")
                        }
                        viewModel.search(searchText.text.trim())
                    },
                    text = {
                        Text(
                            title,
                            maxLines = 1
                        )
                    }
                )
            }
        }
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
            if (state.error != null) {
                ErrorCard(
                    modifier = Modifier.fillMaxSize(),
                    error = state.error ?: "Unbekannter Fehler",
                )
            } else if (!state.hasSearched) {
                EmptyState(
                    title = "Suche nach Spielen",
                    message = "Gib einen Suchbegriff ein, um Spiele zu finden.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SearchResultContent(
                    pagingItems = pagingItems,
                    onGameClick = { game ->
                        android.util.Log.d(
                            "Navigation",
                            "Navigiere zu DetailScreen mit gameId=${game.id}"
                        )
                        navController.navigate(Routes.detail(game.id))
                    },
                    modifier = Modifier.fillMaxSize(),
                    imageQuality = imageQuality
                )
            }
        }
    }
    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false }
        ) {
            FilterBottomSheet(
                platforms = state.platforms,
                genres = state.genres,
                selectedPlatforms = state.selectedPlatforms,
                selectedGenres = state.selectedGenres,
                rating = state.rating,
                ordering = state.ordering,
                isLoadingPlatforms = state.isLoadingPlatforms,
                isLoadingGenres = state.isLoadingGenres,
                platformsError = state.platformsError,
                genresError = state.genresError,
                isOffline = isOffline,
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