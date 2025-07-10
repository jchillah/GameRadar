package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun ShareButton(game: Game, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            // TODO wenn Zeit ist nochmal schauen wie das mit dem DeepLink geht
            // https://gameradar.deeplink/${game.slug}
            val deepLink = "gameradarapp://game/${game.slug}"
            val webLink = "https://rawg.io/games/${game.slug}"
            val shareText = buildString {
                append("Schau dir dieses Spiel an: ${game.title}\n")
                // TODO wenn Zeit ist nochmal schauen wie das mit dem DeepLink geht
                //       append("Öffne direkt in der App: $deepLink\n")
                append("Oder im Browser: $webLink")
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Spiel teilen"))
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Share, contentDescription = "Teilen")
    }
}

@Preview(showBackground = true)
@Composable
fun ShareButtonPreview() {
    ShareButton(
        game = Game(
            id = 1,
            slug = "example-slug",
            title = "Beispielspiel",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.2f,
            description = "Beschreibung des Beispielspiels.",
            metacritic = 85,
            website = "https://example.com",
            esrbRating = "E",
            genres = listOf("Action", "Adventure"),
            platforms = listOf("PC", "PS5"),
            developers = listOf("Beispiel Dev"),
            publishers = listOf("Beispiel Pub"),
            tags = listOf("Indie", "Open World"),
            screenshots = listOf("https://example.com/screenshot1.jpg"),
            stores = listOf("Steam"),
            playtime = 20
        )
    )
}