package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.paging.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

/**
 * Hauptinhalt für Suchergebnisse mit Paging und verschiedenen Zuständen.
 *
 * Features:
 * - Paging-basierte Spielergebnisse
 * - Loading-States mit Shimmer-Platzhaltern
 * - Error-Handling mit Retry-Funktion
 * - Empty-State bei keinen Ergebnissen
 * - Optimierte LazyColumn-Performance
 * - Favoriten- und Wunschlisten-Integration
 * - Anpassbare Bildqualität
 * - Accessibility-Unterstützung
 *
 * @param pagingItems Paging-Items für die Spielergebnisse
 * @param onGameClick Callback beim Klick auf ein Spiel
 * @param modifier Modifier für das Layout
 * @param imageQuality Qualitätseinstellung für Spielbilder
 * @param favoriteIds Set der favorisierten Spiel-IDs
 * @param wishlistIds Set der Wunschlisten-Spiel-IDs
 * @param onWishlistChanged Callback bei Wunschlisten-Änderungen
 */
@Composable
fun SearchResultContent(
    pagingItems: LazyPagingItems<Game>,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
    imageQuality: ImageQuality = ImageQuality.HIGH,
    favoriteIds: Set<Int> = emptySet(),
    wishlistIds: Set<Int> = emptySet(),
    onWishlistChanged: (Game, Boolean) -> Unit = { _, _ -> },
) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            ShimmerPlaceholder(modifier = modifier)
        }
        is LoadState.Error -> {
            val error = (pagingItems.loadState.refresh as LoadState.Error).error
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.error_unknown) + ": ${error.localizedMessage}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { pagingItems.retry() }) {
                    Text(stringResource(R.string.action_retry))
                }
            }
        }
        else -> {
            if (pagingItems.itemCount == 0) {
                EmptyState(
                    title = stringResource(R.string.no_results),
                    message = stringResource(R.string.no_results_message),
                    icon = Icons.Default.SearchOff,
                    modifier = modifier
                )
            } else {
                PerformanceOptimizedLazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pagingItems.itemCount, key = { idx -> pagingItems[idx]?.id ?: idx }) {
                            idx,
                        ->
                        pagingItems[idx]?.let { game ->
                            GameItem(
                                game = game,
                                onClick = { onGameClick(game) },
                                imageQuality = imageQuality,
                                isFavorite = favoriteIds.contains(game.id),
                                showFavoriteIcon = true,
                                isInWishlist = wishlistIds.contains(game.id),
                                onWishlistChanged = { checked ->
                                    onWishlistChanged(game, checked)
                                },
                                showWishlistButton = true
                            )
                        }
                    }
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Loading(
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
