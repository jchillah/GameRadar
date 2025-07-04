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
    isDarkTheme: Boolean,
    setDarkTheme: (Boolean) -> Unit,
    cacheSize: Int,
    maxCacheSize: Int = 1000,
    isOffline: Boolean,
    lastSyncTime: Long?,
    // Neue Einstellungen
    notificationsEnabled: Boolean = true,
    setNotificationsEnabled: (Boolean) -> Unit = {},
    autoRefreshEnabled: Boolean = true,
    setAutoRefreshEnabled: (Boolean) -> Unit = {},
    imageQuality: ImageQuality = ImageQuality.HIGH,
    setImageQuality: (ImageQuality) -> Unit = {},
    language: String = "Deutsch",
    setLanguage: (String) -> Unit = {},
    clearCache: () -> Unit = {},
    aboutApp: () -> Unit = {},
    privacyPolicy: () -> Unit = {},
    // Gaming-Features
    gamingModeEnabled: Boolean = false,
    setGamingModeEnabled: (Boolean) -> Unit = {},
    performanceModeEnabled: Boolean = true,
    setPerformanceModeEnabled: (Boolean) -> Unit = {},
    shareGamesEnabled: Boolean = true,
    setShareGamesEnabled: (Boolean) -> Unit = {},
    optimizeCache: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val settingsViewModel: SettingsViewModel = org.koin.androidx.compose.koinViewModel()
    val showAboutDialog by settingsViewModel.showAboutDialog.collectAsState()
    val showPrivacyDialog by settingsViewModel.showPrivacyDialog.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        SettingsHeader()

        // Cache-Status
        CacheBanner(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheSize,
            maxCacheSize = maxCacheSize,
        )

        IntelligentCacheIndicator(
            modifier = Modifier.fillMaxWidth(),
            isOffline = isOffline,
            cacheSize = cacheSize,
            lastSyncTime = lastSyncTime,
        )

        NetworkErrorHandler(
            modifier = Modifier.fillMaxWidth(),
            isOffline = isOffline,
        )

        // Erweiterte Cache-Verwaltung
        CacheManagementCard(
            modifier = Modifier.fillMaxWidth(),
            cacheSize = cacheSize,
            maxCacheSize = maxCacheSize,
            lastSyncTime = lastSyncTime,
            onClearCache = clearCache,
            onOptimizeCache = optimizeCache
        )

        // Erscheinungsbild
        SettingsSection(title = "Erscheinungsbild") {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Dunkles Design aktivieren",
                checked = isDarkTheme,
                onCheckedChange = setDarkTheme
            )
        }

        // Benachrichtigungen
        SettingsSection(title = "Benachrichtigungen") {
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Push-Benachrichtigungen",
                subtitle = "Neue Spiele und Updates erhalten",
                checked = notificationsEnabled,
                onCheckedChange = setNotificationsEnabled
            )
        }

        // Daten & Synchronisation
        SettingsSection(title = "Daten & Synchronisation") {
            SettingsSwitchItem(
                icon = Icons.Default.Sync,
                title = "Auto-Refresh",
                subtitle = "Automatisch nach neuen Spielen suchen",
                checked = autoRefreshEnabled,
                onCheckedChange = setAutoRefreshEnabled
            )

            SettingsDropdownItem(
                icon = Icons.Default.HighQuality,
                title = "Bildqualität",
                subtitle = "Qualität der Spielbilder",
                selectedValue = imageQuality.displayName,
                onValueChange = { setImageQuality(ImageQuality.fromDisplayName(it)) },
                options = ImageQuality.entries.map { it.displayName }
            )
        }

        // Sprache
        SettingsSection(title = "Sprache") {
            SettingsDropdownItem(
                icon = Icons.Default.Language,
                title = "App-Sprache",
                subtitle = "Sprache der Benutzeroberfläche",
                selectedValue = language,
                onValueChange = setLanguage,
                options = listOf("Deutsch", "English", "Français", "Español")
            )
        }

        // Gaming-Features
        SettingsSection(title = "Gaming-Features") {
            SettingsSwitchItem(
                icon = Icons.Default.Games,
                title = "Gaming-Modus",
                subtitle = "Optimierte Darstellung für Gaming",
                checked = gamingModeEnabled,
                onCheckedChange = setGamingModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Speed,
                title = "Performance-Modus",
                subtitle = "Schnellere Ladezeiten",
                checked = performanceModeEnabled,
                onCheckedChange = setPerformanceModeEnabled
            )

            SettingsSwitchItem(
                icon = Icons.Default.Share,
                title = "Spiele teilen",
                subtitle = "Spiele mit Freunden teilen",
                checked = shareGamesEnabled,
                onCheckedChange = setShareGamesEnabled
            )
        }

        // Über die App
        SettingsSection(title = "Über die App") {
            SettingsButtonItem(
                icon = Icons.Default.Info,
                title = "Über GameFinder",
                subtitle = "Version 1.0.0",
                onClick = aboutApp
            )
            SettingsButtonItem(
                icon = Icons.Default.PrivacyTip,
                title = "Datenschutz",
                subtitle = "Datenschutzerklärung lesen",
                onClick = privacyPolicy
            )
        }
    }

    if (showAboutDialog) {
        AboutAppDialog(onDismiss = { settingsViewModel.dismissAboutDialog() })
    }
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { settingsViewModel.dismissPrivacyDialog() })
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Einstellungen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Passe deine App an",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsDropdownItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = true },
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
        Text(
            text = selectedValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onValueChange(option)
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
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
    SettingsScreen(
        isDarkTheme = true,
        setDarkTheme = {},
        cacheSize = 500,
        isOffline = true,
        maxCacheSize = 100000,
        lastSyncTime = System.currentTimeMillis(),
        notificationsEnabled = true,
        setNotificationsEnabled = {},
        autoRefreshEnabled = true,
        setAutoRefreshEnabled = {},
        imageQuality = ImageQuality.HIGH,
        setImageQuality = {},
        language = "Deutsch",
        setLanguage = {},
        clearCache = {},
        aboutApp = {},
        privacyPolicy = {},
        gamingModeEnabled = false,
        setGamingModeEnabled = {},
        performanceModeEnabled = true,
        setPerformanceModeEnabled = {},
        shareGamesEnabled = true,
        setShareGamesEnabled = {},
        optimizeCache = {}
    )
}