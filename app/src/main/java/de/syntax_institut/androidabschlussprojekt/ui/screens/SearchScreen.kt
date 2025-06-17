package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchBarWithButton
import de.syntax_institut.androidabschlussprojekt.ui.components.search.SearchResultContent
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

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Search Games") }
        )
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

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
        }
    }
}
