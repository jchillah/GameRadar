package de.syntax_institut.androidabschlussprojekt.ui.screens

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
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.favorites.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

/**
 * Zeigt die Favoritenliste mit Statistiken, Export/Import und Löschen-Dialog an.
 *
 * Features:
 * - Statistiken und Export werden für Nicht-Pro-User erst nach Rewarded Ad freigeschaltet
 * - Export/Import via ActivityResultLauncher
 * - Snackbar-Feedback für gesperrte Features
 *
 * @param modifier Modifier für das Layout
 * @param navController Navigation Controller
 * @param viewModel ViewModel für Favoriten
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val imageQuality = settingsState.imageQuality
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    // Unlock-Logik für Export und Statistiken nach Rewarded Ad
    val adsEnabled = settingsState.adsEnabled
    // Unlock-Logik für Export nach Rewarded Ad
    var isExportUnlocked by rememberSaveable { mutableStateOf(false) }
    // Unlock-Logik für Statistiken
    var isStatsUnlocked by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val statsRewardText = stringResource(R.string.rewarded_ad_stats_reward_text)
    val rewardedAdFavoritesRewardText = stringResource(R.string.rewarded_ad_favorites_reward_text)
    // val favoritesExportFilename = stringResource(R.string.favorites_export_filename)
    val favoritesExportFilename = "favoritenliste_export.json"
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))
    val deleteAllContentDescription = stringResource(R.string.dialog_delete_all_favorites_title)
    LaunchedEffect(Unit) {
        AppAnalytics.trackScreenView("FavoritesScreen")
        PerformanceMonitor.startTimer("favorites_screen_load")
        PerformanceMonitor.incrementEventCounter("favorites_screen_opened")
        viewModel.loadFavorites()
    }

    // Performance-Tracking beim Beenden des Screens
    DisposableEffect(Unit) {
        onDispose {
            PerformanceMonitor.endTimer("favorites_screen_load")
            PerformanceMonitor.trackUiRendering("FavoritesScreen", System.currentTimeMillis())

            // Performance-Statistiken abrufen und loggen
            val performanceStats = PerformanceMonitor.getPerformanceStats()
            AppLogger.d("FavoritesScreen", "Performance Stats: $performanceStats")
        }
    }
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            FavoritesHeader(
                hasFavorites = state.favorites.isNotEmpty(),
                onDeleteAllClick = { showDeleteConfirmation = true },
                deleteAllContentDescription = deleteAllContentDescription
            )
        }
        // Statistiken-Button und Chart
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        if (isStatsUnlocked) {
                            // Chart wird angezeigt
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(statsRewardText)
                            }
                        }
                    },
                    enabled = isStatsUnlocked,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.show_stats))
                }
                if (adsEnabled) {
                    RewardedAdButton(
                        adUnitId = "ca-app-pub-3940256099942544/5224354917",
                        adsEnabled = adsEnabled,
                        rewardText = stringResource(R.string.rewarded_ad_stats_reward_text),
                        onReward = { isStatsUnlocked = true }
                    )
                }
            }
        }
        if (isStatsUnlocked && state.favorites.isNotEmpty()) {
            item {
                val genreCounts =
                    state.favorites.flatMap { it.genres }.groupingBy { it }.eachCount()
                GameStatsChart(genreCounts = genreCounts, modifier = Modifier.fillMaxWidth())
            }
        }
        // Favoriten Export/Import-Bar
        item {
            val canUseLauncher = context is ComponentActivity
            val exportLauncher =
                if (canUseLauncher) {
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.CreateDocument("application/json")
                    ) { uri: android.net.Uri? ->
                        uri?.let {
                            coroutineScope.launch {
                                viewModel.exportFavoritesToUri(context, it)
                            }
                        }
                    }
                } else null
            val importLauncher =
                if (canUseLauncher) {
                    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
                        uri?.let {
                            coroutineScope.launch {
                                viewModel.importFavoritesFromUri(context, it)
                            }
                        }
                    }
                } else null
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.favorites_export_import),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (adsEnabled) {
                RewardedAdButton(
                    adUnitId = "ca-app-pub-3940256099942544/5224354917",
                    adsEnabled = adsEnabled,
                    rewardText = stringResource(R.string.rewarded_ad_favorites_reward_text),
                    onReward = { isExportUnlocked = true }
                )
            }
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = {
                    if (isExportUnlocked) {
                        PerformanceMonitor.startTimer("favorites_export_operation")
                        exportLauncher?.launch(favoritesExportFilename)
                        PerformanceMonitor.incrementEventCounter("favorites_export_attempted")
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(rewardedAdFavoritesRewardText)
                        }
                    }
                },
                onImport = {
                    PerformanceMonitor.startTimer("favorites_import_operation")
                    PerformanceMonitor.incrementEventCounter("favorites_import_attempted")
                    importLauncher?.launch(arrayOf("application/json"))
                }
            )
            Box(modifier = Modifier.fillMaxWidth()) { SnackbarHost(hostState = snackbarHostState) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Export/Import-Ergebnisse anzeigen
        state.exportSuccess?.let { success ->
            if (success) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        state.exportMessage ?: "Favoriten erfolgreich exportiert"
                    )
                }
                viewModel.clearExportResult()
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(state.exportMessage ?: "Export fehlgeschlagen")
                }
                viewModel.clearExportResult()
            }
        }

        state.importSuccess?.let { success ->
            if (success) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        state.importMessage ?: "Favoriten erfolgreich importiert"
                    )
                }
                viewModel.clearImportResult()
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(state.importMessage ?: "Import fehlgeschlagen")
                }
                viewModel.clearImportResult()
            }
        }
        // Favoritenliste
        if (state.isLoading) {
            item { Loading(modifier = Modifier.fillMaxSize()) }
        } else if (state.error != null) {
            item {
                ErrorCard(
                        modifier = Modifier.fillMaxSize(),
                        error = state.error ?: Constants.ERROR_UNKNOWN,
                )
            }
        } else if (state.favorites.isEmpty()) {
            item {
                EmptyState(
                        title = stringResource(R.string.favorites_empty_title),
                        message =
                            if (!isOnline) stringResource(R.string.favorites_empty_offline)
                            else stringResource(R.string.favorites_empty_message),
                        icon = Icons.Default.FavoriteBorder,
                        modifier = Modifier.fillMaxSize()
                )
            }
        }
        // Favoritenliste immer anzeigen, wenn sie nicht leer ist!
        if (state.favorites.isNotEmpty()) {
            items(state.favorites) { game ->
                GameItem(
                    game = game,
                    onClick = {
                        AppLogger.d(
                            "Navigation",
                            "Navigiere zu DetailScreen mit gameId=${game.id}"
                        )
                        AppAnalytics.trackEvent(
                            "game_selected",
                            mapOf(
                                "game_id" to game.id,
                                "game_title" to game.title,
                                "source" to "favorites_screen"
                            )
                        )
                        navController.navigateToDetail(game.id)
                    },
                    onDelete = { viewModel.removeFavorite(context, game.id) },
                    imageQuality = imageQuality,
                    showFavoriteIcon = false
                )
            }
        }
    }
    if (showDeleteConfirmation) {
        DeleteFavoritesDialog(
            onConfirm = {
                viewModel.clearAllFavorites(context)
                AppAnalytics.trackEvent(
                    "favorites_cleared",
                    mapOf("count" to state.favorites.size)
                )
                showDeleteConfirmation = false
                // Snackbar anzeigen
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Alle Favoriten wurden gelöscht.")
                }
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
