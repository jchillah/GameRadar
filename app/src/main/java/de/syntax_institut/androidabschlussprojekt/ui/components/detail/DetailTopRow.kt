package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

@Composable
internal fun DetailTopRow(
    game: Game?,
    isFavorite: Boolean,
    navController: NavHostController,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    shareGamesEnabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Zurück"
            )
        }
        Text(
            text = game?.title ?: "Spieldetails",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (game != null) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Neu laden")
            }
            if (shareGamesEnabled) {
                ShareButton(
                    gameTitle = game.title,
                    gameUrl = "https://www.dein-domain.de/game/${game.id}"
                )
            }
            FavoriteButton(
                isFavorite = isFavorite,
                onFavoriteChanged = { _ -> onToggleFavorite() }
            )
        }
    }
}