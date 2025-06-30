package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game

@Composable
fun GameItem(
    game: Game,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(game.imageUrl)
                    .size(Size(160, 160))
                    .crossfade(true)
                    .build(),
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                loading = {
                    Box(
                        modifier = Modifier
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = game.title, style = MaterialTheme.typography.titleMedium)
                game.releaseDate?.let {
                    Text(text = "Release: $it", fontSize = 12.sp)
                }
                Text(text = "Rating: ${game.rating}", fontSize = 12.sp)
            }
            
            // Löschen-Button nur anzeigen, wenn onDelete nicht null ist
            onDelete?.let { deleteFunction ->
                IconButton(
                    onClick = { deleteFunction() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Favorit löschen",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameItemPreview() {
    val game = Game(
        id = 1,
        title = "Sample Game",
        releaseDate = "2021-01-01",
        rating = 4.5F,
        imageUrl = "https://example.com/game_image.jpg",
        description = "This is a sample game description."
    )
    GameItem(game = game, onClick = {})
}

@Preview(showBackground = true)
@Composable
fun GameItemWithDeletePreview() {
    val game = Game(
        id = 1,
        title = "Sample Game",
        releaseDate = "2021-01-01",
        rating = 4.5F,
        imageUrl = "https://example.com/game_image.jpg",
        description = "This is a sample game description."
    )
    GameItem(
        game = game, 
        onClick = {},
        onDelete = {}
    )
}
