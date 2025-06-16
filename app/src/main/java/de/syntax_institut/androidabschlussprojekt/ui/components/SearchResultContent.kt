package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game

@Composable
fun SearchResultContent(
    isLoading: Boolean,
    error: String?,
    games: List<Game>,
    onGameClick: (Game) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fehler: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            LazyColumn {
                items(games) { game ->
                    GameItem(game = game, onClick = { onGameClick(game) })
                }
            }
        }
    }
}
