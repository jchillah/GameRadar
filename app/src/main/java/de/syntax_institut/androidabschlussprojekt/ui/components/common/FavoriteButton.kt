package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun FavoriteButton(
    isFavoriteInitial: Boolean = false,
    onFavoriteChanged: (Boolean) -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(isFavoriteInitial) }

    IconButton(onClick = {
        isFavorite = !isFavorite
        onFavoriteChanged(isFavorite)
    }) {
        Icon(
            painter = painterResource(
                id = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            ),
            contentDescription = if (isFavorite) "Favorit entfernen" else "Zu Favoriten hinzufügen"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonPreview() {
    FavoriteButton()
}