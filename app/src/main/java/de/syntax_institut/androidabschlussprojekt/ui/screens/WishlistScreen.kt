package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
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
    modifier: Modifier = Modifier,
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WishlistHeader()
        WishlistExportImportBar(
            canUseLauncher = canUseLauncher,
            onExport = { exportLauncher?.launch("wishlist_export.json") },
            onImport = { importLauncher?.launch(arrayOf("application/json")) }
        )
        // --- NEU: Suchfeld für Wishlist ---
        var searchText by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<Game>>(emptyList()) }
        // StateFlow für Suchergebnisse
        val searchResultFlow =
            remember(searchText) {
                if (searchText.isNotBlank()) viewModel.searchWishlist(searchText) else null
            }
        LaunchedEffect(searchResultFlow) { searchResultFlow?.collect { searchResults = it } }
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                // Korrektur: Prüfe, ob searchResults.size wirklich ein Int ist (ist korrekt)
                AppAnalytics.trackSearchQuery(it, searchResults.size)
            },
            label = { Text(stringResource(R.string.search_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true // Korrektur: enabled-Parameter ergänzt
        )
        // --- NEU: Button für Wishlist-Count ---
        var wishlistCount by remember { mutableIntStateOf(0) }
        Button(
            onClick = {
                // Hole die Anzahl der Wishlist-Einträge
                AppAnalytics.trackAppFeatureUsage("wishlist_count_button", enabled = true)
                coroutineScope.launch { wishlistCount = viewModel.getWishlistCount() }
            },
            enabled = true
        ) { Text(text = stringResource(R.string.wishlist_count, wishlistCount)) }
        // --- NEU: Button für alle löschen ---
        Button(
            onClick = {
                AppAnalytics.trackAppFeatureUsage("wishlist_clear_all", enabled = true)
                viewModel.clearAllWishlist()
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
            enabled = true
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.wishlist_remove)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.wishlist_clear_all))
        }
        // --- NEU: Optional: Button für getWishlistGameById ---
        var detailIdText by remember { mutableStateOf("") }
        var detailGame by remember { mutableStateOf<Game?>(null) }
        OutlinedTextField(
            value = detailIdText,
            onValueChange = { detailIdText = it },
            label = { Text(stringResource(R.string.wishlist_game_id_for_details)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Button(
            onClick = {
                    val id = detailIdText.toIntOrNull()
                    if (id != null) {
                        viewModel.loadWishlistGameById(id)
                    }
            },
            enabled = true
        ) { Text(stringResource(R.string.wishlist_show_details)) }
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        LaunchedEffect(currentBackStackEntry) {
            // Wenn der Screen verlassen wird, Detail-Game zurücksetzen
            if (currentBackStackEntry?.destination?.route != Routes.WISHLIST) {
                viewModel.clearDetailGame()
            }
        }
        val listToShow = if (searchText.isNotBlank()) searchResults else wishlist
        when {
            isLoading -> {
                LoadingState()
            }
            error != null -> {
                ErrorCard(error = error ?: "")
            }
            listToShow.isEmpty() -> {
                EmptyState(
                    title = stringResource(R.string.wishlist_empty_title),
                    message = stringResource(R.string.wishlist_empty_message),
                    icon = Icons.Default.FavoriteBorder
                )
            }
            else -> {
                listToShow.sortedBy { it.title.lowercase() }.forEach { game ->
                    GameItem(
                        game = game,
                        onClick = { navController.navigateSingleTopTo(Routes.detail(game.id)) },
                        onDelete = {
                            AppAnalytics.trackGameInteraction(
                                game.id.toString(),
                                "wishlist_remove"
                            )
                            viewModel.removeFromWishlist(game.id)
                        },
                        imageQuality =
                            ImageQuality.HIGH, // Optional: oder aus SettingsViewModel holen
                        showFavoriteIcon = false,
                        isInWishlist = wishlist.any { it.id == game.id },
                        onWishlistChanged = { checked ->
                            AppAnalytics.trackGameInteraction(
                                game.id.toString(),
                                "wishlist_toggle"
                            )
                            viewModel.toggleWishlist(game)
                        },
                        showWishlistButton = true
                    )
                }
            }
        }
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
