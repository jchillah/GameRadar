package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
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
    onGameClick: (Game) -> Unit
) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            ShimmerPlaceholder()
        }

        is LoadState.Error -> {
            val error = (pagingItems.loadState.refresh as LoadState.Error).error
            Column(
                modifier = Modifier.fillMaxSize(),
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
                    icon = Icons.Default.SearchOff
                )
            } else {
                PerformanceOptimizedLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(pagingItems.itemCount) { idx ->
                        pagingItems[idx]?.let { game ->
                            GameItem(game = game, onClick = { onGameClick(game) })
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
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
