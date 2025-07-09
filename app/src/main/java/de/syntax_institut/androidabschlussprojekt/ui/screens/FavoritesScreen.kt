package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
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
    val isOnline by NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
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
                text = "Meine Favoriten",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (state.favorites.isNotEmpty()) {
                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.semantics {
                        contentDescription = Constants.DIALOG_DELETE_ALL_FAVORITES_TITLE
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alle löschen")
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
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
                        title = "Keine Favoriten",
                        message = if (!isOnline) "Du bist offline. Deine Favoriten werden angezeigt, sobald du wieder online bist." else "Du hast noch keine Favoriten gespeichert.",
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
                                    android.util.Log.d(
                                        "Navigation",
                                        "Navigiere zu DetailScreen mit gameId=${game.id}"
                                    )
                                    navController.navigateSingleTopTo(Routes.detail(game.id))
                                },
                                onDelete = {
                                    viewModel.removeFavorite(game.id)
                                },
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
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(Constants.DIALOG_DELETE_ALL_FAVORITES_TITLE)
            },
            text = {
                Text(
                    "Möchten Sie wirklich alle ${state.favorites.size} Favoriten löschen? " +
                            "Diese Aktion kann nicht rückgängig gemacht werden."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Alle löschen")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Abbrechen")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreen(
        navController = rememberNavController()
    )
}