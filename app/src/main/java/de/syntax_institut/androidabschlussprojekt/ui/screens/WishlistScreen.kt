package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
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
    val canUseLauncher = context is androidx.activity.ComponentActivity

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
        when {
            isLoading -> {
                LoadingState()
            }

            error != null -> {
                ErrorCard(error = error ?: "")
            }

            wishlist.isEmpty() -> {
                EmptyState(
                    title = stringResource(R.string.wishlist_empty_title),
                    message = stringResource(R.string.wishlist_empty_message),
                    icon = Icons.Default.FavoriteBorder
                )
            }

            else -> {
                wishlist.sortedBy { it.title.lowercase() }.forEach { game ->
                    WishlistGameItem(
                        game = game,
                        onRemove = { viewModel.removeFromWishlist(game.id) },
                        onClick = {
                            navController.navigateSingleTopTo(
                                Routes
                                    .detail(game.id)
                            )
                        }
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
            snackbarHostState.showSnackbar(
                if (it is de.syntax_institut.androidabschlussprojekt.utils.Resource.Success)
                    exportSuccessMsg
                else exportErrorMsg
            )
        }
    }
    LaunchedEffect(importResult) {
        importResult?.let {
            snackbarHostState.showSnackbar(
                if (it is de.syntax_institut.androidabschlussprojekt.utils.Resource.Success)
                    importSuccessMsg
                else importErrorMsg
            )
        }
    }
}

@Composable
fun WishlistGameItem(game: Game, onRemove: () -> Unit, onClick: () -> Unit) {
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
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.wishlist_remove)
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
