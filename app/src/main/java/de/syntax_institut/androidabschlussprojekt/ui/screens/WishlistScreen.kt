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
import androidx.compose.ui.unit.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

/**
 * Zeigt die Wunschliste des Nutzers mit Export/Import, RewardedAd-Integration und Delete-Button in
 * der AppBar.
 *
 * Wird im Navigationsgraph als Ziel für die Wunschliste verwendet. Unterstützt Export/Import und
 * RewardedAd-Logik analog zu Favoriten.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = koinViewModel(),
    navController: NavHostController,
) {
    val wishlist by viewModel.wishlistGames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()
    val importResult by viewModel.importResult.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val canUseLauncher = context is ComponentActivity

    var isExportUnlocked by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val rewardedAdWishlistRewardText = stringResource(R.string.rewarded_ad_wishlist_reward_text)
    val deleteAllContentDescription = stringResource(R.string.wishlist_clear_all)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val imageQuality = settingsState.imageQuality

    // Korrektur: listToShow deklarieren (hier einfach die Wishlist, ggf. mit Suche kombinieren)
    val listToShow = wishlist

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri: Uri? ->
            uri?.let { coroutineScope.launch { viewModel.exportWishlistToUri(context, it) } }
        }
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { coroutineScope.launch { viewModel.importWishlistFromUri(context, it) } }
        }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showClearedSnackbar by remember { mutableStateOf(false) } // NEU

    LaunchedEffect(Unit) { AppAnalytics.trackScreenView("WishlistScreen") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            WishlistHeader(
                hasWishlist = wishlist.isNotEmpty(),
                onDeleteAllClick = { showDeleteConfirmation = true },
                deleteAllContentDescription = deleteAllContentDescription
            )
            Spacer(modifier = Modifier.height(8.dp))
            RewardedAdButton(
                modifier = Modifier.fillMaxWidth(),
                adUnitId = "ca-app-pub-3940256099942544/5224354917",
                adsEnabled = true,
                rewardText = rewardedAdWishlistRewardText,
                onReward = { isExportUnlocked = true },
            )
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = {
                    if (isExportUnlocked) {
                        if (canUseLauncher) exportLauncher.launch("wishlist_export.json")
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(rewardedAdWishlistRewardText)
                        }
                    }
                },
                onImport = {
                    if (canUseLauncher) importLauncher.launch(arrayOf("application/json"))
                }
            )
            // SnackbarHost für Feedback
            Box(modifier = Modifier.fillMaxWidth()) { SnackbarHost(hostState = snackbarHostState) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (isLoading) {
            item {
                LoadingState(modifier = Modifier.fillMaxSize(), message = "Lade Wunschliste...")
            }
        } else if (error != null) {
            item { ErrorCard(error = error ?: "") }
        } else if (listToShow.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.wishlist_empty_title),
                    message = stringResource(R.string.wishlist_empty_message),
                    icon = Icons.Default.FavoriteBorder
                )
            }
        } else {
            items(listToShow.sortedBy { it.title.lowercase() }) { game ->
                GameItem(
                    game = game,
                    onClick = { navController.navigateToDetail(game.id) },
                    onDelete = {
                        AppAnalytics.trackGameInteraction(game.id.toString(), "wishlist_remove")
                        viewModel.removeFromWishlist(game.id)
                    },
                    imageQuality = imageQuality,
                    isInWishlist = true,
                    showWishlistButton = true,
                    showFavoriteIcon = false,
                    onWishlistChanged = { checked -> viewModel.toggleWishlist(game) },
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Bestätigungsdialog für "Wunschliste leeren"
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.wishlist_clear_all)) },
            text = { Text(stringResource(R.string.dialog_delete_all_favorites_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllWishlist()
                        showDeleteConfirmation = false
                        showClearedSnackbar = true // NUR hier setzen!
                    }
                ) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Snackbar für Export/Import-Feedback und Leeren
    val exportSuccessMsg = stringResource(R.string.wishlist_export_success)
    val exportErrorMsg = stringResource(R.string.wishlist_export_error)
    val importSuccessMsg = stringResource(R.string.wishlist_import_success)
    val importErrorMsg = stringResource(R.string.wishlist_import_error)
    val clearAllMsg =
        stringResource(R.string.wishlist_clear_all) +
                " " +
                stringResource(R.string.action_confirm)

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
    LaunchedEffect(showClearedSnackbar) {
        if (showClearedSnackbar) {
            snackbarHostState.showSnackbar(clearAllMsg)
            showClearedSnackbar = false
        }
    }

    val detailGame by viewModel.detailGame.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (detailGame != null) {
        WishlistDetailModal(
            game = detailGame!!,
            onClose = { viewModel.clearDetailGame() },
            onShowDetails = { navController.navigateToDetail(detailGame!!.id) },
            sheetState = sheetState
        )
    }
}
