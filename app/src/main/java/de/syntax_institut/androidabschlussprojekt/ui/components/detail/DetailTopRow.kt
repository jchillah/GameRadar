package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

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
                contentDescription = stringResource(R.string.detail_back)
            )
        }
        Text(
            text = game?.title ?: stringResource(R.string.detail_title_default),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (game != null) {
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.detail_refresh)
                )
            }
            if (shareGamesEnabled) {
                ShareButton(
                    gameTitle = game.title,
                    gameUrl = game.website ?: "https://rawg.io/games/${game.slug}",
                    modifier = Modifier
                )
            }
            FavoriteButton(
                isFavorite = isFavorite,
                onFavoriteChanged = { _ -> onToggleFavorite() }
            )
        }
    }
}