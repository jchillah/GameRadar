package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchBarWithButton
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchResultContent
import de.syntax_institut.androidabschlussprojekt.ui.components.FilterBottomSheet
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import org.koin.androidx.compose.koinViewModel

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

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Search Games") },
            actions = {
                IconButton(onClick = { showFilters = true }) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Filter anzeigen")
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
                    isLoading = state.isLoading,
                    error = state.error,
                    games = state.games,
                    onGameClick = { game ->
                        navController.navigate("detail/${game.id}")
                    }
                )
            }

            // Handle loading states
            if (state.isLoading || state.isPlatformsLoading || state.isGenresLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Handle error states
            if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false }
        ) {
            FilterBottomSheet(
 platforms = state.availablePlatforms,
 genres = state.availableGenres,
                selectedPlatforms = state.selectedPlatforms,
                selectedGenres = state.selectedGenres,
                rating = state.rating,
                onFilterChange = { newPlatforms, newGenres, newRating ->
                    viewModel.updateFilters(newPlatforms, newGenres, newRating)
                    showFilters = false
                }
            )
        }
    }
}
