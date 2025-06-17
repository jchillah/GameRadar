package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.ui.components.ShimmerPlaceholder

@Composable
fun SearchResultContent(
    isLoading: Boolean,
    error: String?,
    games: List<Game>,
    onGameClick: (Game) -> Unit
) {
    if (isLoading) {
        ShimmerPlaceholder()
    } else if (error != null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Fehler: $error", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* retry */ }) {
                Text("Erneut versuchen")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(games) { game ->
                GameItem(game = game, onClick = { onGameClick(game) })
            }
        }
    }
}