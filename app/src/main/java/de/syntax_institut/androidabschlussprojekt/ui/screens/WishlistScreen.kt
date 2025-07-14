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
import de.syntax_institut.androidabschlussprojekt.R
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
        ) { Text(text = "Wishlist Count: $wishlistCount") }
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
            Text(stringResource(R.string.wishlist_export_import))
        }
        // --- NEU: Optional: Button für getWishlistGameById ---
        var detailIdText by remember { mutableStateOf("") }
        var detailGame by remember { mutableStateOf<Game?>(null) }
        OutlinedTextField(
            value = detailIdText,
            onValueChange = { detailIdText = it },
            label = { Text("Game ID für Details") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true // Korrektur: enabled-Parameter ergänzt
        )
        // --- NEU: Detail-Button setzt jetzt den State im ViewModel ---
        Button(
            onClick = {
                    val id = detailIdText.toIntOrNull()
                    if (id != null) {
                        viewModel.loadWishlistGameById(id)
                    }
            },
            enabled = true
        ) { Text("Details anzeigen") }
        // --- NEU: Detail-Game State beim Verlassen des Screens zurücksetzen ---
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        LaunchedEffect(currentBackStackEntry) {
            // Wenn der Screen verlassen wird, Detail-Game zurücksetzen
            if (currentBackStackEntry?.destination?.route != Routes.WISHLIST) {
                viewModel.clearDetailGame()
            }
        }
        // --- Anzeige: Entweder Suchergebnisse oder normale Wishlist ---
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
                    WishlistGameItem(
                        game = game,
                        onRemove = {
                            AppAnalytics.trackGameInteraction(
                                game.id.toString(),
                                "wishlist_remove"
                            )
                            viewModel.removeFromWishlist(game.id)
                        },
                        onClick = { navController.navigateSingleTopTo(Routes.detail(game.id)) },
                        onToggleWishlist = {
                            AppAnalytics.trackGameInteraction(
                                game.id.toString(),
                                "wishlist_toggle"
                            )
                            viewModel.toggleWishlist(game)
                        },
                        isInWishlist = wishlist.any { it.id == game.id }
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
}

// Passe WishlistGameItem an, um einen Toggle-Button zu unterstützen
@Composable
fun WishlistGameItem(
    game: Game,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    onToggleWishlist: (Boolean) -> Unit = {},
    isInWishlist: Boolean = true,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove, enabled = true) { // Korrektur: enabled ergänzt
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.wishlist_remove)
                )
            }
            // Toggle-Button für Wishlist
            IconToggleButton(
                checked = isInWishlist,
                onCheckedChange = onToggleWishlist,
                enabled = true // Korrektur: enabled ergänzt
            ) {
                Icon(
                    imageVector =
                        if (isInWishlist) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription =
                        if (isInWishlist) stringResource(R.string.wishlist_marked)
                        else stringResource(R.string.wishlist_not_marked)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(navController = rememberNavController())
}
