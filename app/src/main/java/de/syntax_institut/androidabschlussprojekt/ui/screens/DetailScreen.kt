package de.syntax_institut.androidabschlussprojekt.ui.screens

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
import de.syntax_institut.androidabschlussprojekt.data.*
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
    modifier: Modifier = Modifier,
    gameId: Int,
    navController: NavHostController,
    vm: DetailViewModel = koinViewModel(key = "detail_$gameId"),
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    val emptyString = ""
    val scrollState = rememberScrollState()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val imageQuality = settingsState.imageQuality
    val shareGamesEnabled = settingsState.shareGamesEnabled

    // Stelle sicher, dass die korrekte gameId verwendet wird
    LaunchedEffect(gameId) {
        AppLogger.d("DetailScreen", "DetailScreen geladen für gameId: $gameId")
        AppAnalytics.trackScreenView("DetailScreen")
        AppAnalytics.trackEvent("game_viewed", mapOf("game_id" to gameId))
        PerformanceMonitor.startTimer("detail_screen_load")
        PerformanceMonitor.incrementEventCounter("detail_screen_opened")
        PerformanceMonitor.trackNavigation(
            "previous_screen",
            "DetailScreen",
            System.currentTimeMillis()
        )
        CrashlyticsHelper.setCustomKey("detail_screen_game_id", gameId)
        CrashlyticsHelper.setCustomKey("current_screen", "DetailScreen")
    }

    // Überwache Änderungen der gameId
    LaunchedEffect(gameId) {
        // Stelle sicher, dass das ViewModel die korrekte gameId verwendet
        if (state.game?.id != gameId) {
            AppLogger.d("DetailScreen", "gameId geändert von ${state.game?.id} zu $gameId")
        }
    }

    LaunchedEffect(state.game) {
        state.game?.let {
            val loadDuration = PerformanceMonitor.endTimer("detail_screen_load")
            PerformanceMonitor.trackMemoryUsage("DetailScreen")
            PerformanceMonitor.trackApiCall("game_detail", loadDuration, true, 0)
        }
    }

    // Performance-Tracking beim Beenden des Screens
    DisposableEffect(Unit) {
        onDispose {
            PerformanceMonitor.trackUiRendering("DetailScreen", System.currentTimeMillis())

            // Performance-Statistiken abrufen und loggen
            val performanceStats = PerformanceMonitor.getPerformanceStats()
            AppLogger.d("DetailScreen", "Performance Stats: $performanceStats")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            DetailTopRow(
                game = state.game,
                isFavorite = state.isFavorite,
                navController = navController,
                onRefresh = {
                    vm.refresh()
                    AppAnalytics.trackUserAction("cache_cleared", gameId)
                    CrashlyticsHelper.setCustomKey("detail_refresh_attempted", true)
                },
                onToggleFavorite = {
                    vm.toggleFavorite()
                    AppAnalytics.trackUserAction("toggle_favorite", gameId)
                    CrashlyticsHelper.setCustomKey("detail_favorite_toggle_attempted", true)
                },
                shareGamesEnabled = shareGamesEnabled,
                isInWishlist = state.isInWishlist,
                onToggleWishlist = {
                    vm.toggleWishlist()
                    CrashlyticsHelper.setCustomKey("detail_wishlist_toggle_attempted", true)
                }
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
            .verticalScroll(scrollState)) {
            when {
                state.isLoading -> {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Lade Spieledetails..."
                    )
                }
                state.error != null -> {
                    val errorText = state.error ?: Constants.ERROR_UNKNOWN
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ErrorCard(
                            modifier = Modifier.padding(16.dp),
                            error = errorText,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                vm.refresh()
                                AppAnalytics.trackUserAction("retry_detail_load", gameId)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
                state.game == null -> {
                    val errorText = stringResource(R.string.error_game_load_failed)
                    Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) {
                        ErrorCard(
                                modifier = Modifier.padding(16.dp),
                                error = errorText,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                                onClick = {
                                    vm.refresh()
                                    AppAnalytics.trackUserAction("retry_detail_load", gameId)
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
                else -> {
                    val game = state.game!!
                    Spacer(modifier = Modifier.height(16.dp))
                    GameHeaderImage(imageUrl = game.imageUrl ?: "", imageQuality = imageQuality)
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
                            onRatingChanged = { newRating -> vm.updateUserRating(newRating) }
                    )
                    SectionCard(stringResource(R.string.game_platforms)) { ChipRow(game.platforms) }
                    SectionCard(stringResource(R.string.game_genres)) { ChipRow(game.genres) }
                    SectionCard(stringResource(R.string.game_developers)) {
                        ChipFlowRow(game.developers)
                    }
                    SectionCard(stringResource(R.string.game_publishers)) {
                        ChipFlowRow(game.publishers)
                    }
                    SectionCard(stringResource(R.string.game_esrb_rating)) {
                        ESRBSection(game.esrbRating)
                    }
                    SectionCard(stringResource(R.string.game_tags)) { ChipFlowRow(game.tags) }
                    SectionCard(stringResource(R.string.game_stores)) { ChipRow(game.stores) }
                    SectionCard(stringResource(R.string.game_metacritic_playtime)) {
                        MetacriticPlaytimeSection(game.metacritic, game.playtime)
                    }
                    SectionCard(stringResource(R.string.game_screenshots)) {
                        ScreenshotGallery(
                                screenshots = game.screenshots,
                                imageQuality = imageQuality
                        )
                        if (game.screenshots.isNotEmpty()) {
                            AppAnalytics.trackEvent(
                                    "screenshots_viewed",
                                    mapOf(
                                        "game_id" to gameId,
                                        "screenshot_count" to game.screenshots.size
                                    )
                            )
                        }
                    }
                    SectionCard(stringResource(R.string.game_trailers)) {
                        // Logge Movies für Debugging
                        LaunchedEffect(game.movies) {
                            AppLogger.d(
                                "DetailScreen",
                                "Movies für ${game.title}: ${game.movies.size} Movies"
                            )
                            AppLogger.d(
                                "DetailScreen",
                                "Game-Objekt Details: ID=${game.id}, Title=${game.title}, Movies=${game.movies}"
                            )
                            game.movies.forEach { movie ->
                                AppLogger.d(
                                    "DetailScreen",
                                    "Movie: ${movie.name}, ID: ${movie.id}, URL: ${
                                        movie.urlMax?.take(
                                            50
                                        )
                                    }..."
                                )
                            }
                        }

                        TrailerGallery(
                                modifier = Modifier.padding(vertical = 8.dp),
                                movies = game.movies,
                                onTrailerClick = { movie ->
                                    movie.urlMax?.let {
                                        AppAnalytics.trackEvent(
                                            "trailer_played",
                                            mapOf(
                                                "game_id" to gameId,
                                                "trailer_name" to movie.name,
                                                "trailer_id" to movie.id
                                            )
                                        )
                                        TrailerPlayerActivity.start(context, it, movie.name)
                                    }
                                },
                            showEmptyState = true,
                            gameHeaderImageUrl = game.imageUrl
                        )
                    }
                    SectionCard(stringResource(R.string.game_website)) {
                        WebsiteSection(game.website, game.id)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(gameId = 1, navController = rememberNavController())
}
