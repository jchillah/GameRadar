package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
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

        SettingsSection(title = "Über die App") {
            SettingsButtonItem(
                icon = Icons.Default.Info,
                title = "Über GameFinder",
                subtitle = "Version 1.0.0",
                onClick = { /* Implement the aboutApp action */ }
            )
            SettingsButtonItem(
                icon = Icons.Default.PrivacyTip,
                title = "Datenschutz",
                subtitle = "Datenschutzerklärung lesen",
                onClick = { /* Implement the privacyPolicy action */ }
            )
        }
    }
}

@Composable
private fun SettingsButtonItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class ImageQuality(val displayName: String) {
    LOW("Niedrig"),
    MEDIUM("Mittel"),
    HIGH("Hoch");

    companion object {
        fun fromDisplayName(displayName: String): ImageQuality {
            return entries.find { it.displayName == displayName } ?: HIGH
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}