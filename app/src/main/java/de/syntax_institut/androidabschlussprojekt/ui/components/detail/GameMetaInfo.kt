package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R
/**
 * Zeigt die wichtigsten Metadaten eines Spiels (Titel, Release, Rating).
 */
@Composable
fun GameMetaInfo(
    title: String,
    releaseDate: String,
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.game_release_date_details, releaseDate), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.game_rating_game_details, rating), style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun GameMetaInfoPreview() {
    GameMetaInfo(title = "Beispielspiel", releaseDate = "2023-01-01", rating = 4.7)
} 