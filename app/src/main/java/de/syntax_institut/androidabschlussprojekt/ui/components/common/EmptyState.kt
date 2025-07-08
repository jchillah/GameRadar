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

/**
 * Zeigt einen leeren Zustand mit Icon, Titel, Nachricht und optionaler Aktion.
 *
 * Folgt Material Design Guidelines für Empty States und bietet
 * eine konsistente Benutzererfahrung in der gesamten App.
 *
 * @param modifier Modifier für das Layout
 * @param title Titel des leeren Zustands
 * @param message Beschreibende Nachricht
 * @param icon Icon für den leeren Zustand
 * @param actionLabel Label für die Aktions-Schaltfläche (optional)
 * @param onAction Callback für die Aktions-Schaltfläche (optional)
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Search,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Leerer Zustand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                if (actionLabel != null && onAction != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    EmptyState(
        title = Constants.EMPTY_STATE_NO_RESULTS,
        message = Constants.EMPTY_STATE_NO_RESULTS_MESSAGE,
        icon = Icons.Default.Search
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyStateWithActionPreview() {
    EmptyState(
        title = Constants.EMPTY_STATE_NO_FAVORITES,
        message = Constants.EMPTY_STATE_NO_FAVORITES_MESSAGE,
        icon = Icons.Default.FavoriteBorder,
        actionLabel = Constants.EMPTY_STATE_DISCOVER_GAMES,
        onAction = { }
    )
}