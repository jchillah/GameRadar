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
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

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

    val exportLauncher =
        if (canUseLauncher) {
            rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("application/json")
            ) { uri: Uri? ->
                uri?.let {
                    coroutineScope.launch { viewModel.exportWishlistToUri(context, it) }
                }
            }
        } else null
    val importLauncher =
        if (canUseLauncher) {
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let {
                    coroutineScope.launch { viewModel.importWishlistFromUri(context, it) }
                }
            }
        } else null

    // Korrektur: listToShow deklarieren (hier einfach die Wishlist, ggf. mit Suche kombinieren)
    val listToShow = wishlist
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isProUser by settingsViewModel.proStatus.collectAsState()
    val adsEnabled by settingsViewModel.adsEnabled.collectAsState()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            WishlistHeader()
            Spacer(modifier = Modifier.height(8.dp))
            if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                RewardedAdButton(
                    adUnitId = "ca-app-pub-3940256099942544/5224354917",
                    adsEnabled = adsEnabled,
                    isProUser = isProUser,
                    rewardText = rewardedAdWishlistRewardText,
                    onReward = { isExportUnlocked = true }
                )
            }
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = {
                    if (isProUser || isExportUnlocked) {
                        exportLauncher?.launch("wishlist_export.json")
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(rewardedAdWishlistRewardText)
                        }
                    }
                },
                onImport = { importLauncher?.launch(arrayOf("application/json")) }
            )
            // SnackbarHost für Feedback
            Box(modifier = Modifier.fillMaxWidth()) { SnackbarHost(hostState = snackbarHostState) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (isLoading) {
            item { LoadingState() }
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
                    onClick = { navController.navigateSingleTopTo(Routes.detail(game.id)) },
                    onDelete = {
                        AppAnalytics.trackGameInteraction(game.id.toString(), "wishlist_remove")
                        viewModel.removeFromWishlist(game.id)
                    },
                    isInWishlist = true,
                    showWishlistButton = true,
                    showFavoriteIcon = false,
                    onWishlistChanged = { checked -> viewModel.toggleWishlist(game) },
                    imageQuality = imageQuality
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Snackbar für Export/Import-Feedback
    val exportSuccessMsg = stringResource(R.string.wishlist_export_success)
    val exportErrorMsg = stringResource(R.string.wishlist_export_error)
    val importSuccessMsg = stringResource(R.string.wishlist_import_success)
    val importErrorMsg = stringResource(R.string.wishlist_import_error)

    LaunchedEffect(exportResult) {
        exportResult?.let {
            // Korrektur: trackCacheOperation erwartet (String, Int, Boolean)
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
            // Korrektur: trackCacheOperation erwartet (String, Int, Boolean)
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

    val detailGame by viewModel.detailGame.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (detailGame != null) {
        WishlistDetailModal(
            game = detailGame!!,
            onClose = { viewModel.clearDetailGame() },
            onShowDetails = {
                navController.navigateSingleTopTo(Routes.detail(detailGame!!.id))
            },
            sheetState = sheetState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(navController = rememberNavController())
}
