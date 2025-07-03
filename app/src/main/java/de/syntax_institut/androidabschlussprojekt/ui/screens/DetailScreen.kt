package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.content.*
import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.core.net.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.androidx.compose.*


/**
 * Zeigt die Detailansicht eines Spiels mit allen relevanten Informationen.
 * @param gameId Die ID des anzuzeigenden Spiels
 * @param navController Navigation Controller für Back-Navigation
 * @param vm ViewModel für die Spieldetails
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    navController: NavHostController,
    vm: DetailViewModel = koinViewModel(),
) {
    val state by vm.uiState.collectAsState()
    val isFavorite by vm.isFavorite.collectAsState()
    val context = LocalContext.current
    val emptyString = ""

    LaunchedEffect(gameId) {
        Analytics.trackScreenView("DetailScreen")
        Analytics.trackEvent("game_viewed", mapOf("game_id" to gameId))
        PerformanceMonitor.startTimer("detail_screen_load")
    }

    LaunchedEffect(gameId) {
        vm.loadDetail(gameId)
    }

    LaunchedEffect(state.game) {
        state.game?.let {
            PerformanceMonitor.endTimer("detail_screen_load")
            PerformanceMonitor.trackMemoryUsage("DetailScreen")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                LoadingState(
                    modifier = Modifier,
                    message = "Lade Spieldetails..."
                )
            }

            state.error != null -> {
                ErrorCard(
                    error = state.error ?: "Unbekannter Fehler",
                    onRetry = {
                        vm.loadDetail(gameId)
                        Analytics.trackUserAction("retry_loading", gameId)
                    }
                )
            }

            else -> {
                val game = state.game
                if (game == null) {
                    ErrorCard(
                        error = "Spiel konnte nicht geladen werden.",
                        onRetry = { vm.loadDetail(gameId) }
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
                            .verticalScroll(rememberScrollState())
                    ) {
                        // TopBar/ActionBar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
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
                                    vm.clearCache()
                                    vm.loadDetail(gameId)
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
                                Text("Metacritic: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            game.playtime?.let {
                                Text(
                                    "Durchschnittliche Spielzeit: $it Std.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        SectionCard("Screenshots") {
                            ScreenshotGallery(game.screenshots)
                            Analytics.trackEvent(
                                "screenshots_viewed", mapOf(
                                    "game_id" to gameId,
                                    "screenshot_count" to game.screenshots.size
                                )
                            )
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
                    }
                }
            }
        }
    }
}