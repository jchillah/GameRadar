package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.favorites.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import de.syntax_institut.androidabschlussprojekt.utils.AppAnalytics.trackEvent
import de.syntax_institut.androidabschlussprojekt.utils.AppAnalytics.trackScreenView
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

/**
 * Shows the favorites list with statistics, export/import, and delete dialog.
 *
 * Features:
 * - Statistics and export are unlocked for non-pro users after watching a rewarded ad
 * - Export/Import via ActivityResultLauncher
 * - Snackbar feedback for locked features
 * - MVVM/MVI architecture with clean separation of concerns
 *
 * @param modifier Modifier for the layout
 * @param navController Navigation controller
 * @param viewModel ViewModel for favorites
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: FavoritesViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
    // State collection
    val state by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    // Local state
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isExportUnlocked by rememberSaveable { mutableStateOf(!settingsState.adsEnabled) }
    var isStatsUnlocked by rememberSaveable { mutableStateOf(!settingsState.adsEnabled) }

    // UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Resources
    // Removed unused variables
    val exportFilename = "favorites_export_${System.currentTimeMillis()}.json"
    val isOnline by NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    // Effects
    LaunchedEffect(Unit) {
        trackScreenView("FavoritesScreen")
        PerformanceMonitor.startTimer("favorites_screen_load")
        viewModel.loadFavorites()
    }

    DisposableEffect(Unit) {
        onDispose {
            PerformanceMonitor.endTimer("favorites_screen_load")
            PerformanceMonitor.trackUiRendering("FavoritesScreen", System.currentTimeMillis())
        }
    }

    // Event Handlers
    val onDeleteAllConfirmed: () -> Unit = {
        viewModel.clearAllFavorites(context)
        trackEvent("favorites_cleared", mapOf("count" to state.favorites.size))
        showDeleteConfirmation = false
        // Show snackbar in a separate coroutine
        coroutineScope.launch {
            snackbarHostState.showSnackbar(context.getString(R.string.favorites_deleted))
        }
        // Explicitly return Unit to match the expected type
        Unit
    }

    val onGameClicked = { game: Game ->
        trackEvent(
            "game_selected",
            mapOf(
                "game_id" to game.id,
                "game_title" to game.title,
                "source" to "favorites_screen"
            )
        )
        navController.navigateToDetail(game.id)
    }

    // UI Composition
    FavoritesScreenContent(
        modifier = modifier,
        state = state,
        settingsState = settingsState,
        isOnline = isOnline,
        isExportUnlocked = isExportUnlocked,
        isStatsUnlocked = isStatsUnlocked,
        onDeleteAllClick = { showDeleteConfirmation = true },
        onExportReward = { isExportUnlocked = true },
        onStatsReward = { isStatsUnlocked = true },
        onGameClicked = onGameClicked,
        onRemoveFavorite = { game -> viewModel.removeFavorite(context, game.id) },
        onImportFavorites = { uri -> viewModel.importFavoritesFromUri(context, uri) },
        onExportFavorites = { uri -> viewModel.exportFavoritesToUri(context, uri) },
        snackbarHostState = snackbarHostState,
        exportFilename = exportFilename
    )

    // Dialogs
    if (showDeleteConfirmation) {
        DeleteFavoritesDialog(
            onConfirm = onDeleteAllConfirmed,
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

/**
 * Stateless composable that contains the UI for the favorites screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreenContent(
    modifier: Modifier = Modifier,
    state: FavoritesUiState,
    settingsState: SettingsUiState,
    isOnline: Boolean,
    isExportUnlocked: Boolean,
    isStatsUnlocked: Boolean,
    onDeleteAllClick: () -> Unit,
    onExportReward: () -> Unit,
    onStatsReward: () -> Unit,
    onGameClicked: (Game) -> Unit,
    onRemoveFavorite: (Game) -> Unit,
    onImportFavorites: (Uri) -> Unit,
    onExportFavorites: (Uri) -> Unit,
    snackbarHostState: SnackbarHostState,
    exportFilename: String,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Export/Import launchers
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onExportFavorites(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onImportFavorites(it) }
    }

    // Handle export/import results
    LaunchedEffect(state.exportSuccess) {
        state.exportSuccess?.let { success ->
            val message = if (success) {
                state.exportMessage ?: context.getString(R.string.export_success)
            } else {
                state.exportMessage ?: context.getString(R.string.export_failed)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(state.importSuccess) {
        state.importSuccess?.let { success ->
            val message = if (success) {
                state.importMessage ?: context.getString(R.string.import_success)
            } else {
                state.importMessage ?: context.getString(R.string.import_failed)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    // Main content
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            FavoritesHeader(
                hasFavorites = state.favorites.isNotEmpty(),
                onDeleteAllClick = onDeleteAllClick,
                deleteAllContentDescription = stringResource(R.string.dialog_delete_all_favorites_title)
            )
        }

        // Statistics Section
        if (state.favorites.isNotEmpty()) {
            item {
                StatisticsSection(
                    isStatsUnlocked = isStatsUnlocked,
                    isAdsEnabled = settingsState.adsEnabled,
                    onStatsClick = { /* Handle stats click */ },
                    onReward = onStatsReward,
                    statsRewardText = stringResource(R.string.rewarded_ad_stats_reward_text),
                    statsButtonText = stringResource(R.string.show_stats)
                )

                if (isStatsUnlocked) {
                    val genreCounts =
                        state.favorites.flatMap { it.genres }.groupingBy { it }.eachCount()
                    GameStatsChart(
                        genreCounts = genreCounts,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Export/Import Section
        item {
            val canUseLauncher = LocalContext.current is ComponentActivity
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.favorites_export_import),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (settingsState.adsEnabled) {
                RewardedAdButton(
                    adsEnabled = settingsState.adsEnabled,
                    rewardText = stringResource(R.string.rewarded_ad_favorites_reward_text),
                    onReward = onExportReward
                )
            }

            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = {
                    if (isExportUnlocked) {
                        PerformanceMonitor.startTimer("favorites_export_operation")
                        PerformanceMonitor.incrementEventCounter("F")
                        exportLauncher.launch(exportFilename)
                    } else {
                        coroutineScope.launch {
                            val exportRewardText = ""
                            snackbarHostState.showSnackbar(exportRewardText)
                        }
                    }
                },
                onImport = {
                    PerformanceMonitor.startTimer("favorites_import_operation")
                    PerformanceMonitor.incrementEventCounter("favorites_import_attempted")
                    importLauncher.launch(arrayOf("application/json"))
                },
                isFavorites = true
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Favorites List
        when {
            state.isLoading -> {
                item { Loading(modifier = Modifier.fillMaxSize()) }
            }

            state.error != null -> {
                item {
                    ErrorCard(
                        modifier = Modifier.fillMaxSize(),
                        error = state.error ?: Constants.ERROR_UNKNOWN
                    )
                }
            }

            state.favorites.isEmpty() -> {
                item {
                    EmptyState(
                        title = stringResource(R.string.favorites_empty_title),
                        message = if (!isOnline) {
                            stringResource(R.string.favorites_empty_offline)
                        } else {
                            stringResource(R.string.favorites_empty_message)
                        },
                        icon = Icons.Default.FavoriteBorder,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            else -> {
                items(state.favorites) { game ->
                    GameItem(
                        game = game,
                        onClick = { onGameClicked(game) },
                        onDelete = { onRemoveFavorite(game) },
                        imageQuality = settingsState.imageQuality,
                        showFavoriteIcon = false
                    )
                }
            }
        }
    }

    // Snackbar Host
    Box(modifier = Modifier.fillMaxWidth()) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

/**
 * Statistics section with unlock button
 */
@Composable
private fun StatisticsSection(
    isStatsUnlocked: Boolean,
    isAdsEnabled: Boolean,
    onStatsClick: () -> Unit,
    onReward: () -> Unit,
    statsRewardText: String,
    statsButtonText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onStatsClick,
            enabled = isStatsUnlocked,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.BarChart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(statsButtonText)
        }

        if (isAdsEnabled && !isStatsUnlocked) {
            Spacer(modifier = Modifier.width(8.dp))
            RewardedAdButton(
                adsEnabled = isAdsEnabled,
                rewardText = statsRewardText,
                onReward = onReward
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreen(navController = rememberNavController())
}

