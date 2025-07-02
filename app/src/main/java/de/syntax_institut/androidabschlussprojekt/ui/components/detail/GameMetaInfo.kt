package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*

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
        Text(releaseDate, style = MaterialTheme.typography.bodyMedium)
        Text("Rating: $rating", style = MaterialTheme.typography.bodySmall)
    }
} 