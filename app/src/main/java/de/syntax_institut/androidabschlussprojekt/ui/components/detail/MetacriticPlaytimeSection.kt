package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt Metacritic-Bewertung und Spielzeit eines Spiels an.
 *
 * Zeigt entweder die Metacritic-Bewertung und/oder Spielzeit an
 * oder einen Platzhalter, wenn keine Daten verfügbar sind.
 *
 * @param metacritic Metacritic-Bewertung (0-100) oder null
 * @param playtime Spielzeit in Stunden oder null
 * @param modifier Modifier für das Layout
 */
@Composable
fun MetacriticPlaytimeSection(metacritic: Int?, playtime: Int?, modifier: Modifier = Modifier) {
    if (metacritic == null && playtime == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp), contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column(modifier = modifier) {
            metacritic?.let {
                Text(
                    stringResource(R.string.game_metacritic_value, it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            playtime?.let {
                Text(
                    stringResource(R.string.game_playtime_value, it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
