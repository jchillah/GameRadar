package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Zeigt eine Fehlerkarte mit Icon, Titel, Nachricht und optionaler Retry-Aktion.
 *
 * Folgt Material Design Guidelines für Error States und bietet
 * eine konsistente Fehlerbehandlung in der gesamten App.
 * Unterstützt Accessibility mit semantischen Beschreibungen.
 *
 * @param modifier Modifier für das Layout
 * @param error Fehlermeldung
 * @param title Titel der Fehlermeldung (optional, Standard: "Fehler aufgetreten")
 * @param showRetryButton Ob ein Retry-Button angezeigt werden soll
 * @param onRetry Callback für Retry-Aktion (optional)
 */
@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    error: String,
    title: String = Constants.ERROR_CARD_DEFAULT_TITLE,
    showRetryButton: Boolean = false,
    onRetry: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Fehler-Icon",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            if (showRetryButton && onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Erneut versuchen")
                }
            }
        }
    }

    // Analytics-Tracking für Fehler
    if (error.isNotBlank()) {
        LaunchedEffect(error) {
            Analytics.trackError(error, "ErrorCard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorCardPreview() {
    ErrorCard(
        error = "Ein unerwarteter Fehler ist aufgetreten. Bitte versuche es später erneut."
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorCardWithRetryPreview() {
    ErrorCard(
        error = "Netzwerkfehler. Überprüfe deine Internetverbindung.",
        showRetryButton = true,
        onRetry = { }
    )
} 