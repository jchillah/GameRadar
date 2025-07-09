package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = org.koin.androidx.compose.koinViewModel(),
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
    val context = LocalContext.current
    val isOnline by NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))
    val gameRepository: GameRepository = org.koin.compose.koinInject()
    var cacheStats by remember {
        mutableStateOf<CacheStats?>(
            null
        )
    }
    var lastSyncTime by remember { mutableStateOf<Long?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cacheStats = gameRepository.getCacheStats()
            lastSyncTime = gameRepository.getLastSyncTime() // Annahme: Funktion existiert
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsHeader()

        CacheBanner(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheStats?.count ?: 0,
            maxCacheSize = 100000,
        )

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
            maxCacheSize = 100000,
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

        // Benachrichtigungs-Sektion ausgelagert
        SettingsSection(title = stringResource(R.string.notifications_section)) {
            SectionNotifications(
                notificationsEnabled = notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
        }

        // Daten & Synchronisation-Sektion ausgelagert
        SettingsSection(title = "Daten & Synchronisation") {
            SectionDataSync(
                autoRefreshEnabled = autoRefreshEnabled,
                imageQuality = imageQuality,
                onAutoRefreshChange = viewModel::setAutoRefreshEnabled,
                onImageQualityChange = viewModel::setImageQuality
            )
        }

        // Sprachsektion ausgelagert
        SettingsSection(title = stringResource(R.string.language_section)) {
            SectionLanguage(
                language = language,
                onLanguageChange = viewModel::setLanguage
            )
        }

        // Gaming-Features-Sektion ausgelagert
        SettingsSection(title = "Gaming-Features") {
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
        SettingsSection(title = "Design") {
            SectionDesign(
                darkModeEnabled = darkModeEnabled,
                onDarkModeChange = viewModel::setDarkModeEnabled
            )
        }

        // "Über die App"-Sektion ausgelagert
        SettingsSection(title = "Über die App") {
            SectionAbout(
                onShowAboutDialog = { showAboutDialog = true },
                onShowPrivacyDialog = { showPrivacyDialog = true }
            )
        }

        // Datenbank-Management-Sektion ausgelagert
        SettingsSection(title = "Datenbank-Management") {
            SectionDatabase()
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