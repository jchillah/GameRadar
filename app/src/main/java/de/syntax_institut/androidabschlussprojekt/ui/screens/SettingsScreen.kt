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
import androidx.core.net.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsHeader()

        CacheBanner(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = 0,
            maxCacheSize = 1,
        )

        IntelligentCacheIndicator(
            modifier = Modifier.fillMaxWidth(),
            isOffline = false,
            cacheSize = 0,
            lastSyncTime = 0,
        )

        NetworkErrorHandler(
            modifier = Modifier.fillMaxWidth(),
            isOffline = false,
        )

        CacheManagementCard(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = 0,
            maxCacheSize = 1,
            lastSyncTime = 0,
            onClearCache = {},
            onOptimizeCache = {}
        )

        SettingsSection(title = "Benachrichtigungen") {
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Push-Benachrichtigungen",
                subtitle = "Neue Spiele und Updates erhalten",
                checked = notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
            val context = LocalContext.current
            Button(
                onClick = {
                    de.syntax_institut.androidabschlussprojekt.MainActivity()
                        .sendNewGameNotification(
                            context,
                            "Testspiel: Notification",
                            "testspiel-notification",
                            999999
                        )
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
                options = listOf("Deutsch", "English", "Français", "Español")
            )
        }

        SettingsSection(title = "Gaming-Features") {
            SettingsSwitchItem(
                icon = Icons.Default.Games,
                title = "Gaming-Modus",
                subtitle = "Optimierte Darstellung für Gaming",
                checked = gamingModeEnabled,
                onCheckedChange = viewModel::setGamingModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Speed,
                title = "Performance-Modus",
                subtitle = "Schnellere Ladezeiten",
                checked = performanceModeEnabled,
                onCheckedChange = viewModel::setPerformanceModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Share,
                title = "Spiele teilen",
                subtitle = "Spiele mit Freunden teilen",
                checked = shareGamesEnabled,
                onCheckedChange = viewModel::setShareGamesEnabled
            )
        }

        SettingsSection(title = "Design") {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "Dunkles Design",
                subtitle = "Aktiviere den Dark Mode",
                checked = darkModeEnabled,
                onCheckedChange = viewModel::setDarkModeEnabled
            )
        }

        SettingsSection(title = "Über die App") {
            SettingsButtonItem(
                icon = Icons.Default.Info,
                title = "Über GameFinder",
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
                subtitle = "support@gamefinder.de",
                onClick = {
                    val intent =
                        android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = "mailto:support@gamefinder.de".toUri()
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Support GameFinder")
                        }
                    context.startActivity(intent)
                }
            )
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