package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*

/**
 * Optimierte Loading-Komponente mit zentriertem CircularProgressIndicator.
 *
 * Features:
 * - Zentrierte Darstellung des Loading-Indikators
 * - Füllt den verfügbaren Platz aus
 * - Material3 CircularProgressIndicator
 * - Einfache Integration in bestehende Layouts
 * - Konsistente Loading-Darstellung in der App
 * - Anpassbare Farbe und Größe
 *
 * @param modifier Modifier für das Layout
 * @param color Farbe des Loading-Indikators (optional)
 */
@Composable
fun Loading(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = color,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    Loading()
} 