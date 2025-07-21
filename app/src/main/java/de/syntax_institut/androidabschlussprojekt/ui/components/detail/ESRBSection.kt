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
 * Zeigt die ESRB-Bewertung eines Spiels an.
 *
 * Zeigt entweder die ESRB-Bewertung als Chip an oder einen
 * Platzhalter mit Hilfe-Icon, wenn keine Bewertung verfügbar ist.
 *
 * @param esrbRating ESRB-Bewertung (z.B. "E", "T", "M") oder null
 * @param modifier Modifier für das Layout
 */
@Composable
fun ESRBSection(esrbRating: String?, modifier: Modifier = Modifier) {
    if (esrbRating.isNullOrBlank()) {
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
        ChipFlowRow(listOf(esrbRating))
    }
}
