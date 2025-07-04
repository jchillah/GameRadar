package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(
    title: String,
    showActions: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    onRefresh: (() -> Unit)? = null,
    isFavorite: Boolean = false,
    onFavoriteChanged: ((Boolean) -> Unit)? = null,
    gameTitle: String? = null,
    gameUrl: String? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"
                )
            }
        },
        actions = {
            if (showActions) {
                if (gameTitle != null && gameUrl != null) {
                    ShareButton(gameTitle = gameTitle, gameUrl = gameUrl)
                }
                onRefresh?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Cache löschen und neu laden"
                        )
                    }
                }
                onFavoriteChanged?.let {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onFavoriteChanged = it,
                        enabled = true
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.graphicsLayer { this.alpha = alpha }
    )
} 