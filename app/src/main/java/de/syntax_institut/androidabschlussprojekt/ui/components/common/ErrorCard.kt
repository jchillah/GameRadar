package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
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
    title: String = stringResource(R.string.error_card_default_title),
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
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = stringResource(R.string.ui_error_icon_content_description),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (showRetryButton && onRetry != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.error_retry),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

    // Analytics-Tracking für Fehler
    if (error.isNotBlank()) {
        LaunchedEffect(error) {
            AppAnalytics.trackError(error, "ErrorCard")
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