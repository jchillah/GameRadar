package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.paging.*
import androidx.paging.compose.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

@Composable
fun SearchResultContent(
    pagingItems: LazyPagingItems<Game>,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
    imageQuality: ImageQuality = ImageQuality.HIGH,
    favoriteIds: Set<Int> = emptySet(),
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
                    "Fehler: ${error.localizedMessage}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { pagingItems.retry() }) {
                    Text("Erneut versuchen")
                }
            }
        }

        else -> {
            if (pagingItems.itemCount == 0) {
                EmptyState(
                    title = "Keine Ergebnisse",
                    message = "Für deine Suche konnten keine Spiele gefunden werden.",
                    icon = Icons.Default.SearchOff,
                    modifier = modifier
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pagingItems.itemCount) { idx ->
                        pagingItems[idx]?.let { game ->
                            GameItem(
                                game = game,
                                onClick = { onGameClick(game) },
                                imageQuality = imageQuality,
                                isFavorite = favoriteIds.contains(game.id)
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
                                LoadingState(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
