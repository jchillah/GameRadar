package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteChanged: (Boolean) -> Unit = {},
    enabled: Boolean = true
) {
    IconButton(
        onClick = {
            if (enabled) {
                onFavoriteChanged(!isFavorite)
            }
        },
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(
                id = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            ),
            contentDescription = if (isFavorite) "Favorit entfernen" else "Zu Favoriten hinzufügen",
            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonPreview() {
    FavoriteButton(isFavorite = false)
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonSelectedPreview() {
    FavoriteButton(isFavorite = true)
}