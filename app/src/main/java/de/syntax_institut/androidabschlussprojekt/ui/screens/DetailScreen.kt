package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.syntax_institut.androidabschlussprojekt.ui.components.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.DetailViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    navController: NavHostController,
    vm: DetailViewModel = koinViewModel()
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(gameId) {
        vm.loadDetail(gameId)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(state.game?.title ?: "") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painterResource(id = android.R.drawable.ic_media_previous),
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                FavoriteButton()
            }
        )
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
            }
            when {
                state.isLoading -> {
                    ShimmerPlaceholder()
                }
                state.error != null -> {
                    Text(
                        text = "Fehler: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    state.game?.let { game ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            GameHeaderImage(imageUrl = game.imageUrl.toString())
                            GameMetaInfo(
                                title = game.title,
                                releaseDate = game.releaseDate ?: "Unbekannt",
                                rating = game.rating.toDouble()
                            )
                            GameDescription(description = game.description)
                        }
                    }
                }
            }
        }
    }
}
