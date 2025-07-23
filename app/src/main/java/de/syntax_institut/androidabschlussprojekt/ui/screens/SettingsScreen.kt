package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
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
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*
import org.koin.compose.*

/**
 * Einstellungsbildschirm mit umfassenden App-Konfigurationsoptionen.
 *
 * Features:
 * - Pro-Status und Werbungsverwaltung
 * - Cache-Management mit Statistiken
 * - Benachrichtigungseinstellungen
 * - Daten-Synchronisation und Bildqualität
 * - Sprachauswahl
 * - Gaming-Features (Gaming-Modus, Performance-Modus)
 * - Datenschutz und Analytics-Einstellungen
 * - Dark Mode
 * - App-Informationen und Datenschutzrichtlinien
 *
 * @param modifier Modifier für das Layout
 * @param viewModel ViewModel für die Einstellungslogik
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        modifier: Modifier = Modifier,
        viewModel: SettingsViewModel = koinViewModel(),
) {
        val settingsState by viewModel.uiState.collectAsState()
        var showAboutDialog by remember { mutableStateOf(false) }
        var showPrivacyDialog by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val gameRepository: GameRepository = koinInject()

        // Überwache Sprachänderungen für sofortige UI-Aktualisierung
        LaunchedEffect(settingsState.language) {
                AppLogger.d("SettingsScreen", "Sprache geändert zu: ${settingsState.language}")
        }

        LaunchedEffect(Unit) {
                AppAnalytics.trackScreenView("SettingsScreen")
                PerformanceMonitor.startTimer("settings_screen_load")
                PerformanceMonitor.incrementEventCounter("settings_screen_opened")

                // Verfügbare Sprachen abrufen
                val availableLanguages = viewModel.getAvailableLanguages()
                AppLogger.d("SettingsScreen", "Verfügbare Sprachen: ${availableLanguages.size}")

                coroutineScope.launch {
                        try {
                                val cacheStats = gameRepository.getCacheStatsMap()
                                val lastSyncTime = gameRepository.getLastSyncTime()
                                val recommendedMaxSize =
                                        CacheUtils.calculateRecommendedMaxCacheSize()
                                viewModel.updateCacheStats(
                                        cacheSize = cacheStats["count"] as? Int ?: 0,
                                        maxCacheSize = recommendedMaxSize,
                                        lastSyncTime = lastSyncTime
                                )

                                // Performance-Tracking für Cache-Operationen
                                PerformanceMonitor.trackCachePerformance(
                                        "cache_stats_retrieval",
                                        cacheStats["count"] as? Int ?: 0,
                                        0.0f, // Default hit rate wenn nicht verfügbar
                                        System.currentTimeMillis()
                                )

                                // Alle Einstellungen für Analytics abrufen
                                val allSettings = viewModel.getAllSettings()
                                AppLogger.d(
                                        "SettingsScreen",
                                        "Alle Einstellungen geladen: ${allSettings.size} Einträge"
                                )
                        } catch (e: Exception) {
                                viewModel.setError(
                                        "Fehler beim Laden der Cache-Statistiken: ${e.message}"
                                )
                                PerformanceMonitor.trackApiCall("cache_stats", 0, false)
                        }
                }
        }

        // Performance-Tracking beim Beenden des Screens
        DisposableEffect(Unit) {
                onDispose {
                        PerformanceMonitor.endTimer("settings_screen_load")
                        PerformanceMonitor.trackUiRendering(
                                "SettingsScreen",
                                System.currentTimeMillis()
                        )

                        // Performance-Statistiken abrufen und loggen
                        val performanceStats = PerformanceMonitor.getPerformanceStats()
                        AppLogger.d("SettingsScreen", "Performance Stats: $performanceStats")
                }
        }

        // Crashlytics-Einstellung überwachen und anwenden
        LaunchedEffect(settingsState.analyticsEnabled) {
                CrashlyticsHelper.setCrashlyticsEnabled(settingsState.analyticsEnabled)
        }

        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Loading-Indikator wenn Einstellungen geladen werden
                if (settingsState.isLoading) {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                }

                // Error-Anzeige wenn Fehler aufgetreten sind
                settingsState.error?.let { error ->
                        Card(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.errorContainer
                                        )
                        ) {
                                Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(16.dp)
                                )
                        }
                }
                // --- Werbung ---
                if (BuildConfig.DEBUG) {
                        Card(
                                modifier =
                                        Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.surfaceVariant
                                        )
                        ) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(16.dp)
                                ) {
                                        Icon(
                                                Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                text = stringResource(R.string.ads_section),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Tooltip(text = stringResource(R.string.ads_section_tooltip))
                                }
                                SettingsSwitchItem(
                                        icon = Icons.Default.EmojiEvents,
                                        title = stringResource(R.string.ads_enabled),
                                        subtitle = stringResource(R.string.ads_enabled_description),
                                        checked = settingsState.adsEnabled,
                                        onCheckedChange = { viewModel.setAdsEnabled(it) }
                                )
                        }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Cache Management ---
                CacheManagementCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        cacheSize = settingsState.cacheSize,
                        maxCacheSize = remember { CacheUtils.calculateRecommendedMaxCacheSize() },
                        lastSyncTime = settingsState.lastSyncTime,
                        onClearCache = {
                                coroutineScope.launch {
                                        try {
                                                PerformanceMonitor.startTimer(
                                                        "cache_clear_operation"
                                                )
                                                gameRepository.clearCache()
                                                val clearDuration =
                                                        PerformanceMonitor.endTimer(
                                                                "cache_clear_operation"
                                                        )

                                                PerformanceMonitor.trackCachePerformance(
                                                        "cache_clear",
                                                        0,
                                                        0f,
                                                        clearDuration
                                                )

                                                val cacheStats = gameRepository.getCacheStatsMap()
                                                val lastSyncTime = gameRepository.getLastSyncTime()
                                                val recommendedMaxSize =
                                                        CacheUtils
                                                                .calculateRecommendedMaxCacheSize()
                                                viewModel.updateCacheStats(
                                                        cacheSize = cacheStats["count"] as? Int
                                                                ?: 0,
                                                        maxCacheSize = recommendedMaxSize,
                                                        lastSyncTime = lastSyncTime
                                                )
                                        } catch (e: Exception) {
                                                viewModel.setError(
                                                        "Fehler beim Leeren des Caches: ${e.message}"
                                                )
                                                PerformanceMonitor.trackApiCall(
                                                        "cache_clear",
                                                        0,
                                                        false
                                                )
                                        }
                                }
                        },
                        onOptimizeCache = {
                                coroutineScope.launch {
                                        try {
                                                PerformanceMonitor.startTimer(
                                                        "cache_optimize_operation"
                                                )
                                                gameRepository.optimizeCache()
                                                val optimizeDuration =
                                                        PerformanceMonitor.endTimer(
                                                                "cache_optimize_operation"
                                                        )

                                                PerformanceMonitor.trackCachePerformance(
                                                        "cache_optimize",
                                                        settingsState
                                                                .cacheSize, // Use current cache
                                                        // size for optimization
                                                        0.8f, // Geschätzte Hit-Rate nach
                                                        // Optimierung
                                                        optimizeDuration
                                                )

                                                val cacheStats = gameRepository.getCacheStatsMap()
                                                val lastSyncTime = gameRepository.getLastSyncTime()
                                                val recommendedMaxSize =
                                                        CacheUtils
                                                                .calculateRecommendedMaxCacheSize()
                                                viewModel.updateCacheStats(
                                                        cacheSize = cacheStats["count"] as? Int
                                                                ?: 0,
                                                        maxCacheSize = recommendedMaxSize,
                                                        lastSyncTime = lastSyncTime
                                                )
                                        } catch (e: Exception) {
                                                viewModel.setError(
                                                        "Fehler bei der Cache-Optimierung: ${e.message}"
                                                )
                                                PerformanceMonitor.trackApiCall(
                                                        "cache_optimize",
                                                        0,
                                                        false
                                                )
                                        }
                                }
                        },
                        onSyncCache = {
                                coroutineScope.launch {
                                        try {
                                                PerformanceMonitor.startTimer(
                                                        "cache_sync_operation"
                                                )
                                                // Sync cache by fetching a sample of games to refresh the cache
                                                coroutineScope.launch {
                                                        try {
                                                                // Refresh cache by fetching a page of games
                                                                // This will update the cache with fresh data
                                                                val games =
                                                                        gameRepository.getGamesByGenre(
                                                                                ""
                                                                        )

                                                                // If we need to ensure the cache is updated, we can also trigger a refresh
                                                                // of specific data like platforms and genres
                                                                gameRepository.getPlatforms()
                                                                gameRepository.getGenres()

                                                                snackbarHostState.showSnackbar("Cache synchronized successfully")
                                                        } catch (e: Exception) {
                                                                // Handle error
                                                                snackbarHostState.showSnackbar("Failed to sync cache: ${e.message ?: "Unknown error"}")
                                                        }
                                                }
                                                val syncDuration =
                                                        PerformanceMonitor.endTimer(
                                                                "cache_sync_operation"
                                                        )
                                                PerformanceMonitor.trackCachePerformance(
                                                        "cache_sync",
                                                        settingsState.cacheSize,
                                                        1.0f,
                                                        syncDuration
                                                )
                                                val cacheStats = gameRepository.getCacheStatsMap()
                                                val lastSyncTime = gameRepository.getLastSyncTime()
                                                val recommendedMaxSize =
                                                        CacheUtils
                                                                .calculateRecommendedMaxCacheSize()
                                                viewModel.updateCacheStats(
                                                        cacheSize = cacheStats["count"] as? Int
                                                                ?: 0,
                                                        maxCacheSize = recommendedMaxSize,
                                                        lastSyncTime = lastSyncTime
                                                )
                                                snackbarHostState.showSnackbar(
                                                        "Cache erfolgreich mit API synchronisiert"
                                                )
                                        } catch (e: Exception) {
                                                viewModel.setError(
                                                        "Fehler bei der Cache-Synchronisation: ${e.message}"
                                                )
                                                PerformanceMonitor.trackApiCall(
                                                        "cache_sync",
                                                        0,
                                                        false
                                                )
                                        }
                                }
                        }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- Sprache, Design, Datenschutz etc. ---
                SettingsSection(title = stringResource(R.string.notifications_section)) {
                        SectionNotifications(
                                notificationsEnabled = settingsState.notificationsEnabled,
                                onCheckedChange = viewModel::setNotificationsEnabled
                        )
                }

                // Daten & Synchronisation-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.data_sync_section)) {
                        SectionDataSync(
                                autoRefreshEnabled = settingsState.autoRefreshEnabled,
                                imageQuality = settingsState.imageQuality,
                                onAutoRefreshChange = viewModel::setAutoRefreshEnabled,
                                onImageQualityChange = viewModel::setImageQuality
                        )
                }

                // Sprachsektion ausgelagert
                SettingsSection(title = stringResource(R.string.language_section)) {
                        SectionLanguage(
                                modifier = modifier,
                                language = settingsState.language,
                                onLanguageChange = viewModel::setLanguage
                        )
                }

                // Gaming-Features-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.gaming_features_section)) {
                        SectionGamingFeatures(
                                gamingModeEnabled = settingsState.gamingModeEnabled,
                                performanceModeEnabled = settingsState.performanceModeEnabled,
                                shareGamesEnabled = settingsState.shareGamesEnabled,
                                onGamingModeChange = viewModel::setGamingModeEnabled,
                                onPerformanceModeChange = viewModel::setPerformanceModeEnabled,
                                onShareGamesChange = viewModel::setShareGamesEnabled
                        )
                }

                // Design-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.design_section)) {
                        SectionDesign(
                                darkModeEnabled = settingsState.darkModeEnabled,
                                onDarkModeChange = viewModel::setDarkModeEnabled
                        )
                }

                // "Über die App"-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.about_app_section)) {
                        SectionAbout(
                                onShowAboutDialog = { showAboutDialog = true },
                                onShowPrivacyDialog = { showPrivacyDialog = true }
                        )
                }

                SettingsSection(title = stringResource(R.string.database_management_section)) {
                        SectionDatabase()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Reset-Button für alle Einstellungen
                        Button(
                                onClick = {
                                        viewModel.resetToDefaults()
                                        coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                        "Alle Einstellungen wurden zurückgesetzt"
                                                )
                                        }
                                },
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                        ),
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Alle Einstellungen zurücksetzen")
                        }
                }

                if (showAboutDialog) {
                        AboutAppDialog(onDismiss = { showAboutDialog = false })
                }
                if (showPrivacyDialog) {
                        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
                }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(hostState = snackbarHostState)
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
        SettingsScreen()
}
