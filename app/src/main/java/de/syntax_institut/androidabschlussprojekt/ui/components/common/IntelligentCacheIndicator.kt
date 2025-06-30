package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun IntelligentCacheIndicator(
    modifier: Modifier = Modifier,
    isOffline: Boolean,
    cacheSize: Int,
    lastSyncTime: Long?,
    onSyncRequest: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOffline)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.CloudSync,
                    contentDescription = if (isOffline) "Offline" else "Online",
                    tint = if (isOffline)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOffline) "Offline-Modus" else "Online-Modus",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isOffline)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isOffline) {
                    "Verwende gecachte Daten ($cacheSize Spiele verfügbar)"
                } else {
                    "Daten werden automatisch synchronisiert"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isOffline)
                    MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )

            lastSyncTime?.let { syncTime ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Letzte Synchronisation: ${formatSyncTime(syncTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOffline)
                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            }

            if (!isOffline) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSyncRequest,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Synchronisieren")
                }
            }
        }
    }
}

private fun formatSyncTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Gerade eben"
        diff < 3600000 -> "${diff / 60000} Minuten"
        diff < 86400000 -> "${diff / 3600000} Stunden"
        else -> "${diff / 86400000} Tage"
    }
}