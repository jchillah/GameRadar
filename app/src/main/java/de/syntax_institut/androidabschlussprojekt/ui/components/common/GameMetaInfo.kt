package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GameMetaInfo(
    title: String,
    releaseDate: String,
    rating: Double
) {
    Column() {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(releaseDate, style = MaterialTheme.typography.bodyMedium)
        Text("Rating: $rating", style = MaterialTheme.typography.bodySmall)
    }
}
@Preview(showBackground = true)
@Composable
fun GameMetaInfoPreview() {
    GameMetaInfo(
        title = "Game Title",
        releaseDate = "Release Date",
        rating = 1.0
    )
}