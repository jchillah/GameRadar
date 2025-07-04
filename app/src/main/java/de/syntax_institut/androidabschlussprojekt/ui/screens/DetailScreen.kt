package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.content.*
import android.util.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.net.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.androidx.compose.*


/**
 * Zeigt die Detailansicht eines Spiels mit allen relevanten Informationen.
 * @param gameId Die ID des anzuzeigenden Spiels
 * @param navController Navigation Controller für Back-Navigation
 * @param modifier Modifier für das Layout des Screens
 * @param vm ViewModel für die Spieldetails
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: DetailViewModel = koinViewModel(),
) {
    val state by vm.uiState.collectAsState()
    val isFavorite by vm.isFavorite.collectAsState()
    val context = LocalContext.current
    val emptyString = ""
    val scrollState = rememberScrollState()

    // Scroll-basierte Animation für die TopAppBar
    val scrollProgress = (scrollState.value / 100f).coerceIn(0f, 1f)
    val topBarAlpha by animateFloatAsState(
        targetValue = if (scrollProgress > 0.1f) 1f else 0f,
        label = "topBarAlpha"
    )

    LaunchedEffect(gameId) {
        Analytics.trackScreenView("DetailScreen")
        Analytics.trackEvent("game_viewed", mapOf("game_id" to gameId))
        PerformanceMonitor.startTimer("detail_screen_load")
    }

    LaunchedEffect(gameId) {
        vm.loadDetail(gameId, forceReload = true)
    }

    LaunchedEffect(state.game) {
        state.game?.let {
            PerformanceMonitor.endTimer("detail_screen_load")
            PerformanceMonitor.trackMemoryUsage("DetailScreen")
        }
    }

    Scaffold(
        topBar = {
            // TopAppBar die beim Scrollen erscheint
            TopAppBar(
                title = {
                    Text(
                        text = state.game?.title ?: "Spieldetails",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(topBarAlpha)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.alpha(topBarAlpha)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    // Actions werden nur angezeigt wenn TopAppBar sichtbar ist
                    if (topBarAlpha > 0.5f) {
                        state.game?.let { game ->
                            ShareButton(
                                gameTitle = game.title,
                                gameUrl = game.website
                            )
                            IconButton(onClick = {
                                vm.loadDetail(gameId, forceReload = true)
                                Analytics.trackUserAction("cache_cleared", gameId)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Cache löschen und neu laden"
                                )
                            }
                            FavoriteButton(
                                isFavorite = isFavorite,
                                onFavoriteChanged = {
                                    vm.toggleFavorite()
                                    Analytics.trackUserAction("toggle_favorite", gameId)
                                },
                                enabled = true
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.alpha(topBarAlpha)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Lade Spieldetails..."
                    )
                }

                state.error != null -> {
                    ErrorCard(
                        error = state.error ?: "Unbekannter Fehler",
                        onRetry = {
                            vm.loadDetail(gameId)
                            Analytics.trackUserAction("retry_loading", gameId)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    val game = state.game
                    if (game == null) {
                        ErrorCard(
                            error = "Spiel konnte nicht geladen werden.",
                            onRetry = { vm.loadDetail(gameId) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Debug-Log für Website
                        LaunchedEffect(game.website) {
                            Log.d("DetailScreen", "Website URL: '${game.website}'")
                            Log.d("DetailScreen", "Website is null: ${game.website == null}")
                            Log.d("DetailScreen", "Website is blank: ${game.website?.isBlank()}")
                            Log.d(
                                "DetailScreen",
                                "Website is not blank: ${game.website?.isNotBlank()}"
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(paddingValues)
                        ) {
                            // Feste TopBar für den Anfang (wird durch TopAppBar ersetzt beim Scrollen)
                            if (topBarAlpha < 0.5f) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Zurück"
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ShareButton(
                                            gameTitle = game.title,
                                            gameUrl = game.website
                                        )
                                        IconButton(onClick = {
                                            vm.loadDetail(gameId, forceReload = true)
                                            Analytics.trackUserAction("cache_cleared", gameId)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Cache löschen und neu laden"
                                            )
                                        }
                                        FavoriteButton(
                                            isFavorite = isFavorite,
                                            onFavoriteChanged = {
                                                vm.toggleFavorite()
                                                Analytics.trackUserAction("toggle_favorite", gameId)
                                            },
                                            enabled = true
                                        )
                                    }
                                }
                            }

                            GameHeaderImage(imageUrl = game.imageUrl.toString())
                            GameMetaInfo(
                                title = game.title,
                                releaseDate = game.releaseDate ?: emptyString,
                                rating = game.rating.toDouble()
                            )
                            GameDescription(description = game.description)
                            Spacer(modifier = Modifier.height(16.dp))
                            GameStatsCard(
                                playtime = game.playtime,
                                metacritic = game.metacritic,
                                userRating = state.userRating,
                                onRatingChanged = { newRating ->
                                    vm.updateUserRating(newRating)
                                }
                            )
                            SectionCard("Plattformen") { ChipRow(game.platforms) }
                            SectionCard("Genres") { ChipRow(game.genres) }
                            SectionCard("Entwickler") { ChipFlowRow(game.developers) }
                            SectionCard("Publisher") { ChipFlowRow(game.publishers) }
                            SectionCard("USK/ESRB") { game.esrbRating?.let { ChipFlowRow(listOf(it)) } }
                            SectionCard("Tags") { ChipFlowRow(game.tags) }
                            SectionCard("Stores") { ChipRow(game.stores) }
                            SectionCard("Metacritic & Spielzeit") {
                                game.metacritic?.let {
                                    Text(
                                        "Metacritic: $it",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                game.playtime?.let {
                                    Text(
                                        "Durchschnittliche Spielzeit: $it Std.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            SectionCard("Screenshots") {
                                if (game.screenshots.isEmpty()) {
                                    if (!NetworkUtils.isNetworkAvailable(context)) {
                                        ErrorCard(
                                            error = "Keine Screenshots verfügbar. Prüfe deine Internetverbindung und versuche es erneut.",
                                            onRetry = { vm.loadDetail(gameId, forceReload = true) }
                                        )
                                    } else {
                                        EmptyState(
                                            title = "Keine Screenshots verfügbar",
                                            message = "Für dieses Spiel wurden keine Screenshots gefunden."
                                        )
                                    }
                                } else {
                                    ScreenshotGallery(game.screenshots)
                                    Analytics.trackEvent(
                                        "screenshots_viewed", mapOf(
                                            "game_id" to gameId,
                                            "screenshot_count" to game.screenshots.size
                                        )
                                    )
                                }
                            }
                            game.website?.takeIf { it.isNotBlank() }?.let { url ->
                                SectionCard("Website") {
                                    TextButton(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                            Analytics.trackUserAction("website_opened", gameId)
                                        }
                                    ) {
                                        Text(
                                            "Website besuchen",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            // Zusätzlicher Spacer am Ende, um sicherzustellen, dass der Inhalt bis ganz unten scrollt
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        gameId = 1,
        navController = rememberNavController()
    )
}