package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
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
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        if (state.platforms.isEmpty()) {
            viewModel.loadPlatforms()
        }
        if (state.genres.isEmpty()) {
            viewModel.loadGenres()
        }
    }

    var selectedTab by remember { mutableIntStateOf(-1) }
    val tabTitles = listOf("Alle", "Neuerscheinungen", "Top-rated")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Spielsuche",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { showFilters = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter anzeigen"
                )
            }
        }
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth(),
            indicator = { tabPositions ->
                if (selectedTab >= 0) TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab])
                )
            },
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier
                        .height(56.dp),
                    selected = selectedTab == index,
                    onClick = {
                        if (selectedTab != index) {
                            selectedTab = index
                            when (index) {
                                0 -> viewModel.updateOrdering("name")
                                1 -> viewModel.updateOrdering("-released")
                                2 -> viewModel.updateOrdering("-rating")
                            }
                            val query =
                                if (searchText.text.isNotBlank()) searchText.text.trim() else " "
                            viewModel.search(query)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    enabled = true
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SearchBarWithButton(
            searchText = searchText,
            onTextChange = {
                searchText = it
                if (it.text.isBlank()) {
                    viewModel.resetSearch()
                }
            },
            onSearchClick = {
                if (searchText.text.isNotBlank()) {
                    viewModel.search(searchText.text.trim())
                }
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
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (!state.hasSearched) {
                EmptyState(
                    title = "Suche nach Spielen",
                    message = "Gib einen Suchbegriff ein, um Spiele zu finden.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SearchResultContent(
                    pagingItems = pagingItems,
                    onGameClick = { game ->
                        navController.navigate(Routes.detail(game.id))
                    }
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
                    showFilters = false
                },
                onRetryPlatforms = { viewModel.loadPlatforms() },
                onRetryGenres = { viewModel.loadGenres() },
                onClearCache = { viewModel.clearCache() }
            )
        }
    }
}