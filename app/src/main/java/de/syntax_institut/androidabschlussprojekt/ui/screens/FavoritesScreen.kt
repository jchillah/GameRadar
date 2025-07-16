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
import de.syntax_institut.androidabschlussprojekt.*
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
 * Zeigt die Favoritenliste mit Statistiken und Löschen-Dialog an.
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
    val rewardedAdFavoritesRewardText = stringResource(R.string.rewarded_ad_favorites_reward_text)
    val state by viewModel.uiState.collectAsState()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()
    val isProUser by settingsViewModel.proStatus.collectAsState()
    val adsEnabled by settingsViewModel.adsEnabled.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    val deleteAllContentDescription = stringResource(R.string.dialog_delete_all_favorites_title)

    // Unlock-Logik für Export nach Rewarded Ad
    var isExportUnlocked by rememberSaveable { mutableStateOf(isProUser) }
    val coroutineScope = rememberCoroutineScope()

    // Unlock-Logik für Statistiken
    var isStatsUnlocked by rememberSaveable { mutableStateOf(isProUser) }
    val statsRewardText = stringResource(R.string.rewarded_ad_stats_reward_text)

    LaunchedEffect(Unit) { viewModel.loadFavorites() }

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
            // Button für Statistiken
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        if (isProUser || isStatsUnlocked) {
                            // Nichts tun, Chart wird angezeigt
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(statsRewardText)
                            }
                        }
                    },
                    enabled = isProUser || isStatsUnlocked,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.show_stats))
                }
                if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                    RewardedAdButton(
                        adUnitId = "ca-app-pub-3940256099942544/5224354917",
                        adsEnabled = adsEnabled,
                        isProUser = isProUser,
                        rewardText = stringResource(R.string.rewarded_ad_stats_reward_text),
                        onReward = { isStatsUnlocked = true }
                    )
                }
            }
        }
        if ((isProUser || isStatsUnlocked) && state.favorites.isNotEmpty()) {
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
            if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                RewardedAdButton(
                    adUnitId = "ca-app-pub-3940256099942544/5224354917",
                    adsEnabled = adsEnabled,
                    isProUser = isProUser,
                    rewardText = stringResource(R.string.rewarded_ad_favorites_reward_text),
                    onReward = { isExportUnlocked = true }
                )
            }
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = {
                    if (isProUser || isExportUnlocked) {
                        exportLauncher?.launch("favoritenliste_export.json")
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(rewardedAdFavoritesRewardText)
                        }
                    }
                },
                onImport = { importLauncher?.launch(arrayOf("application/json")) }
            )
            // SnackbarHost für Feedback
            Box(modifier = Modifier.fillMaxWidth()) { SnackbarHost(hostState = snackbarHostState) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Statistiken-Button für Navigation (optional)
        if (state.favorites.isNotEmpty()) {
            item {
                Button(
                    onClick = { navController.navigateSingleTopTo(Routes.STATS) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Statistiken anzeigen")
                }
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
        } else {
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
