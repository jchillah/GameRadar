package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.navigation.Routes
import de.syntax_institut.androidabschlussprojekt.ui.components.common.CacheInfoCard
import de.syntax_institut.androidabschlussprojekt.ui.components.common.OfflineIndicator
import de.syntax_institut.androidabschlussprojekt.ui.components.search.FilterBottomSheet
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchBarWithButton
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchResultContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel(),
    modifier: Modifier
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
                // Offline-Indikator
                OfflineIndicator(isOffline = isOffline)
                
                // Cache-Info Card (wenn aktiviert)
                if (showCacheInfo) {
                    CacheInfoCard(cacheSize = cacheSize)
                }
                
                SearchBarWithButton(
                    searchText = searchText,
                    onTextChange = { searchText = it },
                    onSearchClick = {
                        if (searchText.text.isNotBlank()) {
                            viewModel.search(searchText.text.trim())
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SearchResultContent(
                    pagingItems = pagingItems,
                    hasSearched = state.hasSearched,
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