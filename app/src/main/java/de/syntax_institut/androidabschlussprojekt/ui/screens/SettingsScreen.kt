package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.app.*
import androidx.core.net.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.*
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cacheStats = gameRepository.getCacheStats()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
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
            lastSyncTime = null,
        )

        NetworkErrorHandler(
            modifier = Modifier.fillMaxWidth(),
            isOffline = !isOnline,
        )

        CacheManagementCard(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheStats?.count ?: 0,
            maxCacheSize = 100000,
            lastSyncTime = null,
            onClearCache = {
                coroutineScope.launch {
                    gameRepository.clearCache()
                    cacheStats = gameRepository.getCacheStats()
                }
            },
            onOptimizeCache = {
                coroutineScope.launch {
                    gameRepository.optimizeCache()
                    cacheStats = gameRepository.getCacheStats()
                }
            }
        )

        SettingsSection(title = Constants.UI_PUSH_NOTIFICATIONS) {
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = Constants.UI_PUSH_NOTIFICATIONS,
                subtitle = Constants.UI_NEW_GAMES_AND_UPDATES,
                checked = notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
            val context = LocalContext.current
            Button(
                onClick = {
                    // Test-Benachrichtigung direkt erstellen
                    val notificationManager =
                        context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    val channelId = Constants.NOTIFICATION_CHANNEL_ID

                    // Channel erstellen falls nicht vorhanden
                    val channel = android.app.NotificationChannel(
                        channelId,
                        Constants.NOTIFICATION_CHANNEL_NAME,
                        android.app.NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)

                    val notification = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(Constants.NOTIFICATION_TITLE_TEST)
                        .setContentText(Constants.NOTIFICATION_TEXT_TEST)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(999999, notification)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test: Neue Spiel-Benachrichtigung")
            }
        }

        SettingsSection(title = "Daten & Synchronisation") {
            SettingsSwitchItem(
                icon = Icons.Default.Sync,
                title = "Auto-Refresh",
                subtitle = "Automatisch nach neuen Spielen suchen",
                checked = autoRefreshEnabled,
                onCheckedChange = viewModel::setAutoRefreshEnabled
            )

            SettingsDropdownItem(
                icon = Icons.Default.HighQuality,
                title = "Bildqualität",
                subtitle = "Qualität der Spielbilder",
                selectedValue = imageQuality.displayName,
                onValueChange = { viewModel.setImageQuality(ImageQuality.fromDisplayName(it)) },
                options = ImageQuality.entries.map { it.displayName }
            )
        }

        SettingsSection(title = "Sprache") {
            SettingsDropdownItem(
                icon = Icons.Default.Language,
                title = "App-Sprache",
                subtitle = "Sprache der Benutzeroberfläche",
                selectedValue = language,
                onValueChange = viewModel::setLanguage,
                options = listOf(
                    Constants.UI_LANGUAGE_GERMAN,
                    Constants.UI_LANGUAGE_ENGLISH,
                    Constants.UI_LANGUAGE_FRENCH,
                    Constants.UI_LANGUAGE_ESPANOL
                )
            )
        }

        SettingsSection(title = "Gaming-Features") {
            SettingsSwitchItem(
                icon = Icons.Default.Games,
                title = Constants.UI_GAMING_MODE,
                subtitle = Constants.UI_GAMING_MODE_DESC,
                checked = gamingModeEnabled,
                onCheckedChange = viewModel::setGamingModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Speed,
                title = Constants.UI_PERFORMANCE_MODE,
                subtitle = Constants.UI_PERFORMANCE_MODE_DESC,
                checked = performanceModeEnabled,
                onCheckedChange = viewModel::setPerformanceModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Share,
                title = Constants.UI_SHARE_GAMES,
                subtitle = Constants.UI_SHARE_GAMES_DESC,
                checked = shareGamesEnabled,
                onCheckedChange = viewModel::setShareGamesEnabled
            )
        }

        SettingsSection(title = "Design") {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = Constants.UI_DARK_MODE,
                subtitle = Constants.UI_DARK_MODE_DESC,
                checked = darkModeEnabled,
                onCheckedChange = viewModel::setDarkModeEnabled
            )
        }

        SettingsSection(title = "Über die App") {
            SettingsButtonItem(
                icon = Icons.Default.Info,
                title = "Über GameRadar",
                subtitle = "Version 1.0.0",
                onClick = { showAboutDialog = true }
            )
            SettingsButtonItem(
                icon = Icons.Default.PrivacyTip,
                title = "Datenschutz",
                subtitle = "Datenschutzerklärung lesen",
                onClick = { showPrivacyDialog = true }
            )
            val context = LocalContext.current
            SettingsButtonItem(
                icon = Icons.Default.Email,
                title = "Support kontaktieren",
                subtitle = Constants.EMAIL,
                onClick = {
                    val intent =
                        android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = "mailto:${Constants.EMAIL}".toUri()
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Support GameRadar")
                        }
                    context.startActivity(intent)
                }
            )
        }

        SettingsSection(title = "Datenbank-Management") {
            var showClearDatabaseDialog by remember { mutableStateOf(false) }

            SettingsButtonItem(
                icon = Icons.Default.DeleteForever,
                title = Constants.DIALOG_RESET_DATABASE_TITLE,
                subtitle = Constants.DIALOG_RESET_DATABASE_SUBTITLE,
                onClick = { showClearDatabaseDialog = true }
            )

            if (showClearDatabaseDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDatabaseDialog = false },
                    title = { Text(Constants.DIALOG_RESET_DATABASE_TITLE) },
                    text = {
                        Text(Constants.DIALOG_RESET_DATABASE_TEXT)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                GameDatabase.clearDatabase(context)
                                showClearDatabaseDialog = false
                            }
                        ) {
                            Text(Constants.DIALOG_DELETE_ALL_CONFIRM)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDatabaseDialog = false }) {
                            Text(Constants.DIALOG_DELETE_ALL_CANCEL)
                        }
                    }
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