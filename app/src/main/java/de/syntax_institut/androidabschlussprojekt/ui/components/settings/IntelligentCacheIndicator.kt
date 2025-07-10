package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
<<<<<<< Updated upstream
=======
import androidx.compose.ui.res.stringResource
>>>>>>> Stashed changes
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun IntelligentCacheIndicator(
    modifier: Modifier = Modifier,
    isOffline: Boolean,
    cacheSize: Int,
    lastSyncTime: Long?,
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
<<<<<<< Updated upstream
                    contentDescription = if (isOffline) stringResource(R.string.offline_mode) else stringResource(
                        R.string.online_mode
                    ),
=======
                    contentDescription = if (isOffline) stringResource(R.string.offline) else stringResource(R.string.online),
>>>>>>> Stashed changes
                    tint = if (isOffline)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
<<<<<<< Updated upstream
                    text = if (isOffline) stringResource(R.string.offline_mode) else stringResource(
                        R.string.online_mode
                    ),
=======
                    text = if (isOffline) stringResource(R.string.offline_mode) else stringResource(R.string.online_mode),
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
                    stringResource(R.string.cache_offline_data, cacheSize)
                } else {
                    stringResource(R.string.cache_sync_auto)
=======
                    stringResource(R.string.using_cached_data, cacheSize)
                } else {
                    stringResource(R.string.data_auto_sync)
>>>>>>> Stashed changes
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
                    text = stringResource(R.string.last_sync_time, formatSyncTime(syncTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOffline)
                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun formatSyncTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> stringResource(R.string.time_just_now)
        diff < 3600000 -> stringResource(R.string.time_minutes_ago, diff / 60000)
        diff < 86400000 -> stringResource(R.string.time_hours_ago, diff / 3600000)
        else -> stringResource(R.string.time_days_ago, diff / 86400000)
    }
}

@Preview(showBackground = true)
@Composable
fun IntelligentCacheIndicatorPreview() {
    IntelligentCacheIndicator(
        isOffline = false,
        cacheSize = 120,
        lastSyncTime = System.currentTimeMillis()
    )
} 