package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*
import org.koin.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinViewModel(),
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val autoRefreshEnabled by viewModel.autoRefreshEnabled.collectAsState()
    val imageQuality by viewModel.imageQuality.collectAsState()
    val language by viewModel.language.collectAsState()
    val gamingModeEnabled by viewModel.gamingModeEnabled.collectAsState()
    val performanceModeEnabled by viewModel.performanceModeEnabled.collectAsState()
    val shareGamesEnabled by viewModel.shareGamesEnabled.collectAsState()
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val exportResult by favoritesViewModel.exportResult.collectAsState()
    val importResult by favoritesViewModel.importResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val canUseLauncher = context is ComponentActivity
    val coroutineScope = rememberCoroutineScope()

    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))
    val gameRepository: GameRepository = koinInject()
    var cacheStats by remember { mutableStateOf<CacheStats?>(null) }
    var lastSyncTime by remember { mutableStateOf<Long?>(null) }
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsState()

    // SAF-Launcher für Export und Import nur, wenn möglich
    val exportLauncher =
        if (canUseLauncher) {
            rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("application/json")
            ) { uri: Uri? ->
                uri?.let {
                    coroutineScope.launch {
                        favoritesViewModel.exportFavoritesToUri(context, it)
                    }
                }
            }
        } else null
    val importLauncher =
        if (canUseLauncher) {
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let {
                    coroutineScope.launch {
                        favoritesViewModel.importFavoritesFromUri(context, it)
                    }
                }
            }
        } else null

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cacheStats = gameRepository.getCacheStats()
            lastSyncTime = gameRepository.getLastSyncTime()
        }
    }

    // Crashlytics-Einstellung überwachen und anwenden
    LaunchedEffect(Unit) {
        // Setze initiale Crashlytics-Einstellung basierend auf Analytics-Opt-In
        val analyticsEnabled = viewModel.analyticsEnabled.value
        CrashlyticsHelper.setCrashlyticsEnabled(analyticsEnabled)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsHeader()

        val recommendedMaxCacheSize = remember { CacheUtils.calculateRecommendedMaxCacheSize() }

        // Debug-Elemente können hier entfernt oder dauerhaft angezeigt werden, falls gewünscht
        // Beispiel: CacheBanner, IntelligentCacheIndicator, NetworkErrorHandler werden immer
        // angezeigt
        CacheBanner(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheStats?.count ?: 0,
            maxCacheSize = recommendedMaxCacheSize,
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {}

        IntelligentCacheIndicator(
            modifier = Modifier.fillMaxWidth(),
            isOffline = !isOnline,
            cacheSize = cacheStats?.count ?: 0,
            lastSyncTime = lastSyncTime,
        )

        NetworkErrorHandler(
            modifier = Modifier.fillMaxWidth(),
            isOffline = !isOnline,
        )

        CacheManagementCard(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheStats?.count ?: 0,
            maxCacheSize = recommendedMaxCacheSize,
            lastSyncTime = lastSyncTime,
            onClearCache = {
                coroutineScope.launch {
                    gameRepository.clearCache()
                    cacheStats = gameRepository.getCacheStats()
                    lastSyncTime = gameRepository.getLastSyncTime()
                }
            },
            onOptimizeCache = {
                coroutineScope.launch {
                    gameRepository.optimizeCache()
                    cacheStats = gameRepository.getCacheStats()
                    lastSyncTime = gameRepository.getLastSyncTime()
                }
            }
        )

        SettingsSection(title = stringResource(R.string.notifications_section)) {
            SectionNotifications(
                notificationsEnabled = notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
        }

        // Daten & Synchronisation-Sektion ausgelagert
        SettingsSection(title = stringResource(R.string.data_sync_section)) {
            SectionDataSync(
                autoRefreshEnabled = autoRefreshEnabled,
                imageQuality = imageQuality,
                onAutoRefreshChange = viewModel::setAutoRefreshEnabled,
                onImageQualityChange = viewModel::setImageQuality
            )
        }

        // Sprachsektion ausgelagert
        SettingsSection(title = stringResource(R.string.language_section)) {
            SectionLanguage(language = language, onLanguageChange = viewModel::setLanguage)
        }

        // Gaming-Features-Sektion ausgelagert
        SettingsSection(title = stringResource(R.string.gaming_features_section)) {
            SectionGamingFeatures(
                gamingModeEnabled = gamingModeEnabled,
                performanceModeEnabled = performanceModeEnabled,
                shareGamesEnabled = shareGamesEnabled,
                onGamingModeChange = viewModel::setGamingModeEnabled,
                onPerformanceModeChange = viewModel::setPerformanceModeEnabled,
                onShareGamesChange = viewModel::setShareGamesEnabled
            )
        }

        // Design-Sektion ausgelagert
        SettingsSection(title = stringResource(R.string.design_section)) {
            SectionDesign(
                darkModeEnabled = darkModeEnabled,
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

        // Analytics-Sektion
        SettingsSection(title = stringResource(R.string.analytics_section)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.analytics_enabled),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.analytics_enabled_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = analyticsEnabled,
                    onCheckedChange = { viewModel.setAnalyticsEnabled(it) },
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor =
                                MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor =
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        // Datenbank-Management und Dialoge werden immer angezeigt (oder nach Wunsch)
        SettingsSection(title = stringResource(R.string.database_management_section)) {
            SectionDatabase()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.wishlist_export_import),
                style = MaterialTheme.typography.titleMedium
            )
            WishlistExportImportBar(
                canUseLauncher = canUseLauncher,
                onExport = { exportLauncher?.launch("wunschliste_export.json") },
                onImport = { importLauncher?.launch(arrayOf("application/json")) }
            )
        }
        // Snackbar für Export/Import-Feedback
        LaunchedEffect(exportResult) {
            exportResult?.let {
                snackbarHostState.showSnackbar(
                    if (it.isSuccess) "Favoriten erfolgreich exportiert!"
                    else
                        "Fehler beim Export: ${it.exceptionOrNull()?.localizedMessage ?: "Unbekannter Fehler"}"
                )
            }
        }
        LaunchedEffect(importResult) {
            importResult?.let {
                snackbarHostState.showSnackbar(
                    if (it.isSuccess) "Favoriten erfolgreich importiert!"
                    else
                        "Fehler beim Import: ${it.exceptionOrNull()?.localizedMessage ?: "Unbekannter Fehler"}"
                )
            }
        }
        if (showAboutDialog) {
            AboutAppDialog(onDismiss = { showAboutDialog = false })
        }
        if (showPrivacyDialog) {
            PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
