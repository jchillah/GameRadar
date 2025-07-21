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
 * Zeigt die Beschreibung eines Spiels mit Fallback-Text.
 *
 * Features:
 * - Anzeige der Spielbeschreibung in lesbarer Formatierung
 * - Fallback-Text bei fehlender Beschreibung
 * - Material3 Typography für konsistente Darstellung
 * - Responsive Layout mit Spacer
 * - Null-Safety für optionale Beschreibungen
 *
 * @param description Die Spielbeschreibung (kann null sein)
 * @param modifier Modifier für das Layout
 */
@Composable
fun GameDescription(description: String?, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        description ?: stringResource(R.string.detail_no_description),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GameDescriptionPreview() {
    GameDescription(description = stringResource(R.string.preview_sectioncard_content))
} 