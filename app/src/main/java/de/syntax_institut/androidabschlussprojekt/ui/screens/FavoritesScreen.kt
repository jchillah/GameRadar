package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.favorites.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.androidx.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    // Fix: stringResource im Composable-Kontext holen
    val deleteAllContentDescription = stringResource(R.string.dialog_delete_all_favorites_title)

    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    Column(modifier = modifier.fillMaxWidth()) {
        FavoritesHeader(
            hasFavorites = state.favorites.isNotEmpty(),
            onDeleteAllClick = { showDeleteConfirmation = true }
        )
        // Statistiken nur anzeigen, wenn Favoriten vorhanden
        if (state.favorites.isNotEmpty()) {
            val genreCounts = state.favorites.flatMap { it.genres }.groupingBy { it }.eachCount()
            GameStatsChart(genreCounts = genreCounts, modifier = Modifier.fillMaxWidth())
        }
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> {
                    Loading(modifier = Modifier.fillMaxSize())
                }
                state.error != null -> {
                    ErrorCard(
                        modifier = Modifier.fillMaxSize(),
                        error = state.error ?: Constants.ERROR_UNKNOWN,
                    )
                }
                state.favorites.isEmpty() -> {
                    EmptyState(
                        title = stringResource(R.string.favorites_empty_title),
                        message =
                            if (!isOnline) stringResource(R.string.favorites_empty_offline)
                            else stringResource(R.string.favorites_empty_message),
                        icon = Icons.Default.FavoriteBorder,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.favorites) { game ->
                            GameItem(
                                game = game,
                                onClick = {
                                    AppLogger.d(
                                        "Navigation",
                                        "Navigiere zu DetailScreen mit gameId=${game.id}"
                                    )
                                    navController.navigateSingleTopTo(Routes.detail(game.id))
                                },
                                onDelete = { viewModel.removeFavorite(context, game.id) },
                                imageQuality = imageQuality,
                                showFavoriteIcon = false
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        DeleteFavoritesDialog(
            onConfirm = {
                viewModel.clearAllFavorites(context)
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreen(navController = rememberNavController())
}
