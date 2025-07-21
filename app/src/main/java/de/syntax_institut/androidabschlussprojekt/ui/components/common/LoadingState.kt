package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Erweiterte Loading-Komponente mit Spinner und anpassbarer Nachricht.
 *
 * Features:
 * - Großer CircularProgressIndicator für bessere Sichtbarkeit
 * - Anpassbare Nachricht für Kontext-spezifisches Feedback
 * - Zentrierte Darstellung mit Padding
 * - Material3 Design-System
 * - Responsive Layout
 * - Accessibility-freundliche Darstellung
 * - Flexible Größenanpassung
 *
 * @param modifier Modifier für das Layout
 * @param message Anzuzeigende Nachricht während des Ladens
 * @param color Farbe des Loading-Indikators (optional)
 * @param showMessage Gibt an, ob die Nachricht angezeigt werden soll (optional)
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_state_default),
    color: Color = MaterialTheme.colorScheme.primary,
    showMessage: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = color
            )
            if (showMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun LoadingStatePreview() {
    LoadingState(
        message = "Lade Daten..."
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun LoadingStateWithoutMessagePreview() {
    LoadingState(
        message = "",
        showMessage = false
    )
}