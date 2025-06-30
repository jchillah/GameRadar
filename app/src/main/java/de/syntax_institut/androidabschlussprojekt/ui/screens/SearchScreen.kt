package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.R
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
    val cacheSize by viewModel.cacheSize.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    var showCacheInfo by remember { mutableStateOf(false) }
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    // Lade Plattformen und Genres beim ersten Start
    LaunchedEffect(Unit) {
        if (state.platforms.isEmpty()) {
            viewModel.loadPlatforms()
        }
        if (state.genres.isEmpty()) {
            viewModel.loadGenres()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Spielsuche") },
            actions = {
                // Cache-Info Button
                IconButton(onClick = { showCacheInfo = !showCacheInfo }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Cache-Info"
                    )
                }
                // Filter Button
                IconButton(onClick = { showFilters = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Filter anzeigen"
                    )
                }
            }
        )
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Cache-Info und Status
                CacheInfoCard(
                    cacheSize = cacheSize,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CacheStatusIndicator(
                    cacheSize = cacheSize,
                    maxCacheSize = 1000,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Network Error Handler
                NetworkErrorHandler(
                    isOffline = isOffline,
                    onRetry = { viewModel.search(searchText.text.trim()) }
                )

                // Intelligenter Cache-Indikator statt einfachem Offline-Indikator
                IntelligentCacheIndicator(
                    isOffline = isOffline,
                    cacheSize = cacheSize,
                    lastSyncTime = state.lastSyncTime,
                    onSyncRequest = { viewModel.clearCache() }
                )
                
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
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!state.hasSearched) {
                    EmptyState(
                        title = "Suche nach Spielen",
                        message = "Gib einen Suchbegriff ein, um Spiele zu finden.",
                        modifier = Modifier.weight(1f)
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