package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

/**
 * Cache-Management-Karte mit Statistiken, Fortschrittsbalken und Aktions-Buttons.
 *
 * Zeigt detaillierte Informationen über den Cache-Status an:
 * - Anzahl gespeicherter Spiele
 * - Speicherverbrauch in MB
 * - Letzte Synchronisation
 * - Cache-Nutzung als Fortschrittsbalken
 * - Sync- und Clear-Cache-Buttons
 * - RewardedAd-Buttons für Nicht-Pro-User
 *
 * Features:
 * - Pro-User-Status-Erkennung
 * - RewardedAd-Integration für Freischaltung
 * - Dynamische Button-Texte je nach Freischaltungsstatus
 * - Snackbar-Feedback für gesperrte Features
 * - Tooltip-Integration für bessere UX
 *
 * @param modifier Modifier für das Layout der Karte
 * @param cacheSize Anzahl der im Cache gespeicherten Spiele
 * @param maxCacheSize Maximale Anzahl der Spiele im Cache
 * @param lastSyncTime Zeitstempel der letzten Synchronisation (null = nie)
 * @param onClearCache Callback für das Leeren des Caches
 * @param onOptimizeCache Callback für die Cache-Optimierung (optional)
 */
@Composable
fun CacheManagementCard(
        modifier: Modifier = Modifier,
        cacheSize: Int,
        maxCacheSize: Int,
        lastSyncTime: Long?,
        onClearCache: () -> Unit,
        onOptimizeCache: () -> Unit = {},
) {
        val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()

    var isSyncUnlocked by rememberSaveable { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
    val rewardedAdSyncRewardText = stringResource(R.string.rewarded_ad_sync_reward_text)

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
                                            text = "${settingsState.cacheUsagePercentage}%",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                            color =
                                                if (settingsState.isCacheFull)
                                                    MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.primary
                                        )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { settingsState.cacheUsagePercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color =
                                        if (settingsState.isCacheFull)
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                            // Cache-Full-Warnung
                            if (settingsState.isCacheFull) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text =
                                            stringResource(
                                                R.string.cache_full_warning
                                            ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Aktions-Buttons
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                OutlinedButton(
                                        onClick = {
                                            if (isSyncUnlocked) {
                                                        onOptimizeCache()
                                                } else {
                                                        coroutineScope.launch {
                                                                snackbarHostState.showSnackbar(
                                                                    rewardedAdSyncRewardText
                                                                )
                                                        }
                                                }
                                        },
                                    enabled = isSyncUnlocked,
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

                    // Rewarded Ad Buttons für Freischaltung
                    if (settingsState.adsEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Rewarded Ad für Synchronisation
                            RewardedAdButton(
                                adUnitId = "ca-app-pub-3940256099942544/5224354917",
                                adsEnabled = settingsState.adsEnabled,
                                rewardText =
                                    if (isSyncUnlocked)
                                        stringResource(
                                            R.string
                                                .rewarded_ad_sync_unlocked_text
                                        )
                                    else rewardedAdSyncRewardText,
                                onReward = { isSyncUnlocked = true }
                            )


                        }
                    }

                        SnackbarHost(hostState = snackbarHostState)
                }
        }
}

/**
 * Einzelnes Cache-Statistik-Element mit Icon, Wert und Label.
 *
 * Zeigt eine einzelne Cache-Metrik in einem vertikalen Layout an:
 * - Icon oben
 * - Wert in der Mitte (fett)
 * - Label unten (kleiner Text)
 *
 * @param icon Das Icon für die Statistik
 * @param label Das Label/der Name der Statistik
 * @param value Der Wert der Statistik als String
 */
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

/**
 * Preview für die CacheManagementCard.
 *
 * Zeigt eine Vorschau der CacheManagementCard mit Beispieldaten an. Verwendet für die Entwicklung
 * und das Testing der UI-Komponente.
 */
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
