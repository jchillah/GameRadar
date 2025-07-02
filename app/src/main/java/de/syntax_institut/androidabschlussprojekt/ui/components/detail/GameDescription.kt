package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

/**
 * Zeigt die Beschreibung eines Spiels.
 */
@Composable
fun GameDescription(description: String?, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        description ?: "No description available.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
} 