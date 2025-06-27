package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import de.syntax_institut.androidabschlussprojekt.ui.components.common.ShimmerPlaceholder

@Composable
fun SearchResultContent(
    pagingItems: LazyPagingItems<Game>,
    hasSearched: Boolean,
    onGameClick: (Game) -> Unit
) {
    when {
        hasSearched && pagingItems.loadState.refresh is LoadState.Loading -> {
            ShimmerPlaceholder()
        }
        hasSearched && pagingItems.loadState.refresh is LoadState.Error -> {
            val error = (pagingItems.loadState.refresh as LoadState.Error).error
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Fehler: ${error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { pagingItems.retry() }) {
                    Text("Erneut versuchen")
                }
            }
        }
        hasSearched -> {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pagingItems.itemCount) { idx ->
                    pagingItems[idx]?.let { game ->
                        GameItem(game = game, onClick = { onGameClick(game) })
                    }
                }
                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        ShimmerPlaceholder()
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bitte gib einen Suchbegriff ein.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
