package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.net.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
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
    val shareGamesEnabled by settingsViewModel.shareGamesEnabled.collectAsState()

    LaunchedEffect(gameId) {
        AppAnalytics.trackScreenView("DetailScreen")
        AppAnalytics.trackEvent("game_viewed", mapOf("game_id" to gameId))
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            DetailTopRow(
                game = state.game,
                isFavorite = isFavorite,
                navController = navController,
                onRefresh = {
                    vm.loadDetail(
                        gameId,
                        forceReload = true
                    ); AppAnalytics.trackUserAction("cache_cleared", gameId)
                },
                onToggleFavorite = {
                    vm.toggleFavorite(); AppAnalytics.trackUserAction(
                    "toggle_favorite",
                    gameId
                )
                },
                shareGamesEnabled = shareGamesEnabled
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .verticalScroll(scrollState)
        ) {
            when (val res = state.resource) {
                is Resource.Loading<Game> -> {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Lade Spieldetails..."
                    )
                }

                is Resource.Error<Game> -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ErrorCard(
                            modifier = Modifier.padding(16.dp),
                            error = res.message ?: Constants.ERROR_UNKNOWN,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                vm.loadDetail(gameId, forceReload = true)
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

                is Resource.Success<Game> -> {
                    val game = res.data
                    if (game == null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ErrorCard(
                                modifier = Modifier.padding(16.dp),
                                error = "Spiel konnte nicht geladen werden.",
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    vm.loadDetail(gameId, forceReload = true)
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
                    } else {
                        // Entferne überflüssige Website-Logs
                        // LaunchedEffect(game.website) {
                        //     Log.d("DetailScreen", "Website URL: '${game.website}'")
                        //     Log.d("DetailScreen", "Website is null: "+(game.website == null))
                        //     Log.d("DetailScreen", "Website is blank: "+(game.website?.isBlank()))
                        //     Log.d("DetailScreen", "Website is not blank: "+(game.website?.isNotBlank()))
                        // }
                        Spacer(modifier = Modifier.height(16.dp))
                        GameHeaderImage(
                            imageUrl = game.imageUrl ?: "",
                            imageQuality = imageQuality
                        )
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
                        SectionCard(stringResource(R.string.game_platforms)) { ChipRow(game.platforms) }
                        SectionCard(stringResource(R.string.game_genres)) { ChipRow(game.genres) }
                        SectionCard(stringResource(R.string.game_developers)) { ChipFlowRow(game.developers) }
                        SectionCard(stringResource(R.string.game_publishers)) { ChipFlowRow(game.publishers) }
                        SectionCard(stringResource(R.string.game_esrb_rating)) { 
                            if (game.esrbRating.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = Icons.AutoMirrored.Filled.Help,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.no_rating),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                ChipFlowRow(listOf(game.esrbRating))
                            }
                        }
                        SectionCard(stringResource(R.string.game_tags)) { ChipFlowRow(game.tags) }
                        SectionCard(stringResource(R.string.game_stores)) { ChipRow(game.stores) }
                        SectionCard(stringResource(R.string.game_metacritic_playtime)) {
                            if (game.metacritic == null && game.playtime == null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = Icons.AutoMirrored.Filled.Help,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.no_rating),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Column {
                                    game.metacritic?.let {
                                        Text(
                                            stringResource(R.string.game_metacritic_value, it),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    game.playtime?.let {
                                        Text(
                                            stringResource(R.string.game_playtime_value, it),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        SectionCard(stringResource(R.string.game_screenshots)) {
                            ScreenshotGallery(
                                screenshots = game.screenshots,
                                imageQuality = imageQuality
                            )
                            if (game.screenshots.isNotEmpty()) {
                                AppAnalytics.trackEvent(
                                    "screenshots_viewed", mapOf(
                                        "game_id" to gameId,
                                        "screenshot_count" to game.screenshots.size
                                    )
                                )
                            }
                        }
                        SectionCard(stringResource(R.string.game_trailers)) {
                            TrailerGallery(
                                modifier = Modifier.padding(vertical = 8.dp),
                                movies = game.movies,
                                onTrailerClick = { movie ->
                                    movie.urlMax?.let {
                                        TrailerPlayerActivity.start(
                                            context,
                                            it,
                                            movie.name
                                        )
                                    }
                                },
                                showEmptyState = true
                            )
                        }
                        SectionCard(stringResource(R.string.game_website)) {
                            if (game.website.isNullOrBlank() || !(game.website.startsWith("http://") || game.website.startsWith("https://"))) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.game_no_website),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                TextButton(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, game.website.toUri())
                                        context.startActivity(intent)
                                        AppAnalytics.trackUserAction("website_opened", gameId)
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

                else -> {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Lade Spieldetails..."
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        gameId = 1,
        navController = rememberNavController()
    )
}


