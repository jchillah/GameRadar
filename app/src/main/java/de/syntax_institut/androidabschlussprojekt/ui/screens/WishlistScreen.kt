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
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.components.wishlist.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

/**
 * Zeigt die Wunschliste des Nutzers mit Export/Import, RewardedAd-Integration und Delete-Button.
 *
 * Features:
 * - Anzeige der Wunschliste mit Spielen
 * - Export/Import der Wunschliste als JSON
 * - Rewarded Ad Integration zum Freischalten des Exports
 * - Löschen von einzelnen Spielen oder der gesamten Wunschliste
 * - Bottom Sheet mit Spieldetails
 *
 * @param viewModel Das zugehörige ViewModel für die Wunschliste
 * @param navController Der NavController für die Navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = koinViewModel(),
    navController: NavHostController,
) {
    // State aus dem ViewModel sammeln
    val wishlist by viewModel.wishlistGames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()
    val importResult by viewModel.importResult.collectAsState()
    val detailGame by viewModel.detailGame.collectAsState()

    // UI State
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val canUseLauncher = context is ComponentActivity
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Einstellungen
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val isProUser = !settingsState.adsEnabled
    val adsEnabled = settingsState.adsEnabled
    val imageQuality = settingsState.imageQuality

    // Lokaler State
    var isExportUnlocked by rememberSaveable { mutableStateOf(isProUser) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showClearedSnackbar by remember { mutableStateOf(false) }

    // Strings aus den Ressourcen
    val rewardedAdWishlistRewardText = stringResource(R.string.rewarded_ad_wishlist_reward_text)
    val deleteAllContentDescription = stringResource(R.string.wishlist_clear_all)

    // Launcher für Dateioperationen
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { coroutineScope.launch { viewModel.exportWishlistToUri(context, it) } }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { coroutineScope.launch { viewModel.importWishlistFromUri(context, it) } }
    }

    // Effekte
    LaunchedEffect(Unit) {
        AppAnalytics.trackScreenView("WishlistScreen")
    }

    LaunchedEffect(isProUser) {
        if (isProUser) {
            isExportUnlocked = true
        }
    }

    // Haupt-UI
    WishlistScreenContent(
        wishlist = wishlist,
        isLoading = isLoading,
        error = error,
        isProUser = isProUser,
        adsEnabled = adsEnabled,
        imageQuality = imageQuality,
        canUseLauncher = canUseLauncher,
        // Removed unused parameter
        showDeleteConfirmation = showDeleteConfirmation,
        detailGame = detailGame,
        sheetState = sheetState,
        snackbarHostState = snackbarHostState,
        onDeleteAllClick = { showDeleteConfirmation = true },
        onDismissDeleteDialog = { showDeleteConfirmation = false },
        onConfirmDelete = {
            viewModel.clearAllWishlist()
            showDeleteConfirmation = false
            showClearedSnackbar = true
        },
        onReward = { isExportUnlocked = true },
        onExport = {
            if (isProUser || isExportUnlocked) {
                if (canUseLauncher) exportLauncher.launch("wishlist_export_${System.currentTimeMillis()}.json")
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(rewardedAdWishlistRewardText)
                }
            }
        },
        onImport = {
            if (canUseLauncher) importLauncher.launch(arrayOf("application/json"))
        },
        onGameClick = { game -> navController.navigateToDetail(game.id) },
        onRemoveGame = { game ->
            AppAnalytics.trackGameInteraction(game.id.toString(), "wishlist_remove")
            viewModel.removeFromWishlist(game.id)
        },
        onToggleWishlist = { game -> viewModel.toggleWishlist(game) },
        onCloseDetail = { viewModel.clearDetailGame() },
        onShowDetails = { game -> navController.navigateToDetail(game.id) }
    )

    // Snackbar-Nachrichten
    val exportSuccessMsg = stringResource(R.string.wishlist_export_success)
    val exportErrorMsg = stringResource(R.string.wishlist_export_error)
    val importSuccessMsg = stringResource(R.string.wishlist_import_success)
    val importErrorMsg = stringResource(R.string.wishlist_import_error)
    val clearAllMsg = stringResource(R.string.wishlist_clear_all_success)

    // Snackbar für Export/Import-Feedback
    LaunchedEffect(exportResult) {
        exportResult?.let {
            AppAnalytics.trackCacheOperation(
                "wishlist_export",
                wishlist.size,
                it is Resource.Success
            )
            snackbarHostState.showSnackbar(
                if (it is Resource.Success) exportSuccessMsg else exportErrorMsg
            )
        }
    }

    LaunchedEffect(importResult) {
        importResult?.let {
            AppAnalytics.trackCacheOperation(
                "wishlist_import",
                wishlist.size,
                it is Resource.Success
            )
            snackbarHostState.showSnackbar(
                if (it is Resource.Success) importSuccessMsg else importErrorMsg
            )
        }
    }

    // Snackbar für erfolgreiches Leeren der Wunschliste
    LaunchedEffect(showClearedSnackbar) {
        if (showClearedSnackbar) {
            snackbarHostState.showSnackbar(clearAllMsg)
            showClearedSnackbar = false
        }
    }
}

/**
 * Stateless UI-Komponente für den WishlistScreen.
 * Enthält die gesamte UI-Logik ohne Business-Logik.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WishlistScreenContent(
    wishlist: List<Game>,
    isLoading: Boolean,
    error: String?,
    isProUser: Boolean,
    adsEnabled: Boolean,
    imageQuality: ImageQuality,
    canUseLauncher: Boolean,
    // Removed unused parameter
    showDeleteConfirmation: Boolean,
    detailGame: Game?,
    sheetState: SheetState,
    snackbarHostState: SnackbarHostState,
    onDeleteAllClick: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onReward: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onGameClick: (Game) -> Unit,
    onRemoveGame: (Game) -> Unit,
    onToggleWishlist: (Game) -> Unit,
    onCloseDetail: () -> Unit,
    onShowDetails: (Game) -> Unit,
) {
    val deleteAllContentDescription = stringResource(R.string.wishlist_clear_all)
    val rewardedAdWishlistRewardText = stringResource(R.string.rewarded_ad_wishlist_reward_text)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            WishlistHeader(
                hasWishlist = wishlist.isNotEmpty(),
                onDeleteAllClick = onDeleteAllClick,
                deleteAllContentDescription = deleteAllContentDescription
            )
            Spacer(modifier = Modifier.height(8.dp))

            if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                RewardedAdButton(
                    modifier = Modifier.fillMaxWidth(),
                    adsEnabled = adsEnabled,
                    rewardText = rewardedAdWishlistRewardText,
                    onReward = onReward,
                )
            }
            
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = onExport,
                onImport = onImport
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                SnackbarHost(hostState = snackbarHostState)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            isLoading -> {
                item {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.loading_wishlist)
                    )
                }
            }

            error != null -> {
                item {
                    ErrorCard(
                        modifier = Modifier.fillMaxWidth(),
                        error = error.ifEmpty { stringResource(R.string.error_unknown) }
                    )
                }
            }

            wishlist.isEmpty() -> {
                item {
                    EmptyState(
                        title = stringResource(R.string.wishlist_empty_title),
                        message = stringResource(R.string.wishlist_empty_message),
                        icon = Icons.Default.FavoriteBorder,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            else -> {
                items(wishlist.sortedBy { it.title.lowercase() }) { game ->
                    GameItem(
                        game = game,
                        onClick = { onGameClick(game) },
                        onDelete = { onRemoveGame(game) },
                        imageQuality = imageQuality,
                        isInWishlist = true,
                        showWishlistButton = true,
                        showFavoriteIcon = false,
                        onWishlistChanged = { _ -> onToggleWishlist(game) }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Bestätigungsdialog für "Wunschliste leeren"
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            title = { Text(stringResource(R.string.wishlist_clear_all)) },
            text = { Text(stringResource(R.string.dialog_delete_all_favorites_text)) },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Bottom Sheet für Spieldetails
    if (detailGame != null) {
        WishlistDetailModal(
            game = detailGame,
            onClose = onCloseDetail,
            onShowDetails = { onShowDetails(detailGame) },
            sheetState = sheetState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(navController = rememberNavController())
}
