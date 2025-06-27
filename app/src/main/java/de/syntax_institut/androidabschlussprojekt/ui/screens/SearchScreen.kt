package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilters by remember { mutableStateOf(false) }
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Search Games") },
            actions = {
                IconButton(onClick = { showFilters = true }) {
                    Icon(Icons.TwoTone.MoreVert, contentDescription = "Filter anzeigen")
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
                onOrderingChange = { newOrdering ->
                    viewModel.updateOrdering(newOrdering)
                },
                onFilterChange = { newPlatforms, newGenres, newRating ->
                    viewModel.updateFilters(newPlatforms, newGenres, newRating)
                    showFilters = false
                }
            )
        }
    }
}