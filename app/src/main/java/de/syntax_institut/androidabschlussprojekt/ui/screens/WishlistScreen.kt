package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
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
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val canUseLauncher = context is ComponentActivity

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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Korrektur: WishlistHeader nur mit erlaubten Parametern aufrufen
            WishlistHeader()
            Spacer(modifier = Modifier.height(8.dp))
            // Export/Import-Bar korrekt einbauen
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = { exportLauncher?.launch("wishlist_export.json") },
                onImport = { importLauncher?.launch(arrayOf("application/json")) }
            )
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
                    onWishlistChanged = { checked -> viewModel.toggleWishlist(game) }
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
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearDetailGame() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = detailGame!!.title, style = MaterialTheme.typography.titleLarge)
                if (!detailGame!!.imageUrl.isNullOrBlank()) {
                    // Bild anzeigen (z.B. mit Coil)
                    Image(
                        painter =
                            coil3.compose.rememberAsyncImagePainter(detailGame!!.imageUrl),
                        contentDescription = detailGame!!.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Text(
                    text = detailGame!!.description
                        ?: stringResource(R.string.detail_no_description),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 6,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.clearDetailGame()
                            navController.navigateSingleTopTo(Routes.detail(detailGame!!.id))
                        }
                    ) { Text(stringResource(R.string.wishlist_full_details)) }
                    OutlinedButton(onClick = { viewModel.clearDetailGame() }) {
                        Text(stringResource(R.string.action_close))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(navController = rememberNavController())
}
