package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt die wichtigsten Metadaten eines Spiels in einer strukturierten Ansicht.
 *
 * Features:
 * - Spieltitel in großer Typografie
 * - Release-Datum mit lokalisiertem Format
 * - Bewertung mit Dezimalstellen
 * - Material3 Typography-System
 * - Responsive Layout
 * - Accessibility-freundliche Struktur
 *
 * @param title Titel des Spiels
 * @param releaseDate Release-Datum als String
 * @param rating Bewertung als Double (0.0 - 5.0)
 * @param modifier Modifier für das Layout
 */
@Composable
fun GameMetaInfo(
    modifier: Modifier = Modifier,
    title: String,
    releaseDate: String,
    rating: Double,
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(R.string.game_release_date_details, releaseDate),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            stringResource(R.string.game_rating_game_details, rating),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameMetaInfoPreview() {
    GameMetaInfo(
        title = stringResource(R.string.preview_settings_button_title),
        releaseDate = "2023-01-01",
        rating = 4.7
    )
} 