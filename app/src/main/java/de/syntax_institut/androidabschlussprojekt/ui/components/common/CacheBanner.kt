package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun CacheBanner(
    cacheSize: Int,
    maxCacheSize: Int = 1000,
    modifier: Modifier = Modifier,
) {
    val cachePercentage = (cacheSize.toFloat() / maxCacheSize) * 100f
    val isCacheFull = cachePercentage >= 90f

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCacheFull)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "Cache",
                tint = if (isCacheFull)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cache: $cacheSize/$maxCacheSize Spiele",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCacheFull)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                LinearProgressIndicator(
                    progress = { cachePercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = if (isCacheFull)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    trackColor = if (isCacheFull)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isCacheFull) "Cache fast voll" else "Offline verfügbar",
                style = MaterialTheme.typography.labelSmall,
                color = if (isCacheFull)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
} 