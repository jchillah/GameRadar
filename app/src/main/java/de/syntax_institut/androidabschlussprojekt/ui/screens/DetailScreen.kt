package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.content.*
import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
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
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val imageQuality by settingsViewModel.imageQuality.collectAsState()

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

    val game = state.game
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            DetailTopRow(
                game = game,
                isFavorite = isFavorite,
                navController = navController,
                onRefresh = {
                    vm.loadDetail(
                        gameId,
                        forceReload = true
                    ); Analytics.trackUserAction("cache_cleared", gameId)
                },
                onToggleFavorite = {
                    vm.toggleFavorite(); Analytics.trackUserAction(
                    "toggle_favorite",
                    gameId
                )
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .verticalScroll(scrollState)
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
                        modifier = Modifier.fillMaxSize(),
                        error = state.error ?: "Unbekannter Fehler",
                    )
                }

                game == null -> {
                    ErrorCard(
                        modifier = Modifier.fillMaxSize(),
                        error = "Spiel konnte nicht geladen werden.",
                    )
                }

                else -> {
                    LaunchedEffect(game.website) {
                        Log.d("DetailScreen", "Website URL: '${game.website}'")
                        Log.d("DetailScreen", "Website is null: ${game.website == null}")
                        Log.d("DetailScreen", "Website is blank: ${game.website?.isBlank()}")
                        Log.d(
                            "DetailScreen",
                            "Website is not blank: ${game.website?.isNotBlank()}"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // wenn die Daten nicht Null sind
                    if (true) {
                        GameHeaderImage(
                            imageUrl = game.imageUrl ?: "",
                            imageQuality = imageQuality
                        )
                    }

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
                                )
                            } else {
                                ErrorCard(
                                    error = "Für dieses Spiel wurden keine Screenshots gefunden.",
                                )
                            }
                        } else {
                            ScreenshotGallery(game.screenshots, imageQuality = imageQuality)
                            Analytics.trackEvent(
                                "screenshots_viewed", mapOf(
                                    "game_id" to gameId,
                                    "screenshot_count" to game.screenshots.size
                                )
                            )
                        }
                    }
                    if (game.website.isNullOrBlank()) {
                        SectionCard("Website") {
                            ErrorCard(
                                error = "Keine Website verfügbar.",
                            )
                        }
                    } else {
                        SectionCard("Website") {
                            TextButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, game.website.toUri())
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
                    Spacer(modifier = Modifier.height(80.dp))
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


