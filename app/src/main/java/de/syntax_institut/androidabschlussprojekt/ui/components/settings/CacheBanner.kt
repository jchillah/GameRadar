@file:OptIn(ExperimentalMaterial3Api::class)

package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheBanner(
    modifier: Modifier = Modifier,
    cacheSize: Int = 0,
    maxCacheSize: Int = 100000,
) {
    val safeMaxCacheSize = if (maxCacheSize == 0) 1 else maxCacheSize
    val cachePercentage = (cacheSize.toFloat() / safeMaxCacheSize) * 100f
    val isCacheFull = cachePercentage >= 90f
    val isCacheWarning = cachePercentage >= 70f && cachePercentage < 90f
    val isUnlimited = maxCacheSize >= 100000
    val displayMax = if (isUnlimited) "∞" else maxCacheSize.toString()
    val containerColor = when {
        isCacheFull -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        isCacheWarning -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 1f)
    }
    val textColor = when {
        isCacheFull -> MaterialTheme.colorScheme.error
        isCacheWarning -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    val progressColor = when {
        isCacheFull -> MaterialTheme.colorScheme.error
        isCacheWarning -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    val label = when {
        isCacheFull -> "Performance kritisch"
        isCacheWarning -> "Performance eingeschränkt"
        else -> "Performance optimal"
    }
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
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
                    tint = progressColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cache: $cacheSize/$displayMax Spiele",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                    LinearProgressIndicator(
                        progress = { cachePercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = progressColor,
                        trackColor = progressColor.copy(alpha = 0.2f),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        Surface(
                            tonalElevation = 8.dp,
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Cache-Info",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Der Cache speichert Spiele für Offline-Nutzung und schnellere Ladezeiten.\n\n" +
                                            "Farben:\n" +
                                            "• Grün: Performance optimal (bis 70%)\n" +
                                            "• Gelb: Performance eingeschränkt (ab 70%)\n" +
                                            "• Rot: Performance kritisch (ab 90%)\n\n" +
                                            "Das Unendlich-Zeichen (∞) bedeutet, dass der Cache nicht limitiert ist und nur durch den Gerätespeicher begrenzt wird.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    state = tooltipState
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cache-Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CacheBannerPreview() {
    CacheBanner(cacheSize = 800, maxCacheSize = 100000)
} 