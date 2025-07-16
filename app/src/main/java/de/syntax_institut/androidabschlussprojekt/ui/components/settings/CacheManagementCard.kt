package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@Composable
fun CacheManagementCard(
        modifier: Modifier = Modifier,
        cacheSize: Int,
        maxCacheSize: Int,
        lastSyncTime: Long?,
        onClearCache: () -> Unit,
        onOptimizeCache: () -> Unit = {},
) {
        val safeMaxCacheSize = if (maxCacheSize == 0) 1 else maxCacheSize
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val isProUser by settingsViewModel.proStatus.collectAsState()
        val adsEnabled by settingsViewModel.adsEnabled.collectAsState()

        Card(
                modifier = modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.cache_section),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Tooltip(text = stringResource(R.string.cache_section_tooltip))
                        }

                        // Cache-Statistiken
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                CacheStatItem(
                                        icon = Icons.Default.Games,
                                        label = stringResource(R.string.cache_stat_saved_games),
                                        value = "$cacheSize"
                                )
                                CacheStatItem(
                                        icon = Icons.Default.Storage,
                                        label = stringResource(R.string.cache_stat_storage),
                                        value = "${(cacheSize * 0.5).toInt()} MB"
                                )
                                CacheStatItem(
                                        icon = Icons.Default.Schedule,
                                        label = stringResource(R.string.cache_stat_last_sync),
                                        value =
                                                if (lastSyncTime != null)
                                                        stringResource(R.string.cache_stat_today)
                                                else stringResource(R.string.cache_stat_never)
                                )
                        }

                        // Fortschrittsbalken
                        Column {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text = stringResource(R.string.cache_stat_usage),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                                text =
                                                        "${(cacheSize.toFloat() / safeMaxCacheSize * 100).toInt()}%",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                        progress = { cacheSize.toFloat() / safeMaxCacheSize },
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                        }

                        // Aktions-Buttons
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                OutlinedButton(
                                        onClick = onOptimizeCache,
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                ButtonDefaults.outlinedButtonColors(
                                                        contentColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Sync,
                                                contentDescription =
                                                        stringResource(R.string.sync_favorites),
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.sync_favorites))
                                }

                                Button(
                                        onClick = onClearCache,
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.error,
                                                        contentColor =
                                                                MaterialTheme.colorScheme.onError
                                                )
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.ClearAll,
                                                contentDescription =
                                                        stringResource(R.string.clear_cache),
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.clear_cache))
                                }
                        }
                        if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                                RewardedAdButton(
                                        adUnitId = "ca-app-pub-3940256099942544/5224354917",
                                        adsEnabled = adsEnabled,
                                        isProUser = isProUser,
                                        rewardText =
                                                stringResource(
                                                        R.string.rewarded_ad_cache_reward_text
                                                ),
                                        onReward = onOptimizeCache
                                )
                        }
                }
        }
}

@Composable
private fun CacheStatItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String,
) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                )
                Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
}

@Composable
@Preview(showBackground = true)
fun CacheManagementCardPreview() {
        CacheManagementCard(
                cacheSize = 150,
                maxCacheSize = 100000,
                lastSyncTime = System.currentTimeMillis(),
                onClearCache = {},
                onOptimizeCache = {}
        )
}
