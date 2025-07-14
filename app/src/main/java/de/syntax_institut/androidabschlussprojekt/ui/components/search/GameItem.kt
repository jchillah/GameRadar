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
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*

@Composable
fun GameItem(
        game: Game,
        onClick: () -> Unit,
        onDelete: (() -> Unit)? = null,
        imageQuality: ImageQuality = ImageQuality.HIGH,
        isFavorite: Boolean = false,
        showFavoriteIcon: Boolean = true,
        isInWishlist: Boolean = false,
        onWishlistChanged: ((Boolean) -> Unit)? = null,
        showWishlistButton: Boolean = true,
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
                        val size =
                                when (imageQuality) {
                                        ImageQuality.LOW -> Size(160, 90)
                                        ImageQuality.MEDIUM -> Size(320, 180)
                                        ImageQuality.HIGH -> Size.ORIGINAL
                                }
                        SubcomposeAsyncImage(
                                model =
                                        ImageRequest.Builder(context)
                                                .data(game.imageUrl)
                                                .size(size)
                                                .crossfade(true)
                                                .build(),
                                contentDescription = game.title,
                                contentScale = ContentScale.Crop,
                                modifier =
                                        Modifier
                                                .width(160.dp)
                                                .aspectRatio(16f / 9f)
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                loading = {
                                        Box(
                                                modifier =
                                                        Modifier
                                                                .width(160.dp)
                                                                .aspectRatio(16f / 9f),
                                                contentAlignment = Alignment.Center
                                        ) { Loading(modifier = Modifier.size(24.dp)) }
                                },
                                error = {
                                        Box(
                                                modifier =
                                                        Modifier
                                                                .width(160.dp)
                                                                .aspectRatio(16f / 9f)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.BrokenImage,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        modifier = Modifier.size(32.dp)
                                                )
                                        }
                                }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = game.title,
                                        style = MaterialTheme.typography.titleMedium
                                )
                                game.releaseDate?.let {
                                        Text(
                                                text =
                                                        stringResource(
                                                                R.string
                                                                        .game_release_date_search_gameitem,
                                                                it
                                                        ),
                                                fontSize = 12.sp
                                        )
                                }
                                Text(
                                        text =
                                                stringResource(
                                                        R.string.game_rating_search_gameitem,
                                                        game.rating
                                                ),
                                        fontSize = 12.sp
                                )
                        }

                        // Herz-Icon für Favoritenstatus
                        if (showFavoriteIcon) {
                                Icon(
                                        imageVector =
                                                if (isFavorite) Icons.Default.Favorite
                                                else Icons.Default.FavoriteBorder,
                                        contentDescription =
                                                if (isFavorite)
                                                        stringResource(R.string.favorite_marked)
                                                else stringResource(R.string.favorite_not_marked),
                                        tint =
                                                if (isFavorite) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                )
                        }
                        // Wishlist-Button (Stern)
                        if (showWishlistButton && onWishlistChanged != null) {
                                WishlistButton(
                                        isInWishlist = isInWishlist,
                                        onWishlistChanged = onWishlistChanged,
                                        modifier = Modifier.size(28.dp)
                                )
                        }
                        // Löschen-Button nur anzeigen, wenn onDelete nicht null ist
                        onDelete?.let { deleteFunction ->
                                IconButton(onClick = { deleteFunction() }) {
                                        Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription =
                                                        stringResource(R.string.favorite_delete),
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
        val game =
                Game(
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
        val game =
                Game(
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
        GameItem(game = game, onClick = {}, onDelete = {})
}
