package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

@Composable
fun GameItem(
    game: Game,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    imageQuality: ImageQuality = ImageQuality.HIGH,
    isFavorite: Boolean = false,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val size = when (imageQuality) {
                ImageQuality.LOW -> Size(80, 80)
                ImageQuality.MEDIUM -> Size(160, 160)
                ImageQuality.HIGH -> Size.ORIGINAL
            }
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(game.imageUrl)
                    .size(size)
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
                        Loading(modifier = Modifier.size(24.dp))
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

            // Herz-Icon für Favoritenstatus
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Als Favorit markiert" else "Nicht als Favorit",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
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
        description = "This is a sample game description.",
        slug = "sample-game",
        metacritic = 80,
        website = "https://example.com",
        esrbRating = "E",
        genres = listOf("Action", "Adventure"),
        platforms = listOf("PC", "PS5"),
        developers = listOf("Sample Dev"),
        publishers = listOf("Sample Pub"),
        tags = listOf("Indie", "Open World"),
        screenshots = listOf("https://example.com/screenshot1.jpg"),
        stores = listOf("Steam"),
        playtime = 15
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
        description = "This is a sample game description.",
        slug = "sample-game",
        metacritic = 80,
        website = "https://example.com",
        esrbRating = "E",
        genres = listOf("Action", "Adventure"),
        platforms = listOf("PC", "PS5"),
        developers = listOf("Sample Dev"),
        publishers = listOf("Sample Pub"),
        tags = listOf("Indie", "Open World"),
        screenshots = listOf("https://example.com/screenshot1.jpg"),
        stores = listOf("Steam"),
        playtime = 15
    )
    GameItem(
        game = game, 
        onClick = {},
        onDelete = {}
    )
}
