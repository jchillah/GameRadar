package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R


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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.empty_state_icon_content_description),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (actionLabel != null && onAction != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onAction,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            actionLabel,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}