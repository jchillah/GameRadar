package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import de.syntax_institut.androidabschlussprojekt.data.*

/**
 * Gaming-Features Sektion für die Einstellungen.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
@Composable
fun SectionGamingFeatures(
    modifier: Modifier = Modifier,
    gamingModeEnabled: Boolean,
    performanceModeEnabled: Boolean,
    shareGamesEnabled: Boolean,
    onGamingModeChange: (Boolean) -> Unit,
    onPerformanceModeChange: (Boolean) -> Unit,
    onShareGamesChange: (Boolean) -> Unit,
) {
    Column(modifier = modifier) {
        SettingsSwitchItem(
            icon = Icons.Default.Games,
            title = Constants.UI_GAMING_MODE,
            subtitle = Constants.UI_GAMING_MODE_DESC,
            checked = gamingModeEnabled,
            onCheckedChange = onGamingModeChange
        )

        SettingsSwitchItem(
            icon = Icons.Default.Speed,
            title = Constants.UI_PERFORMANCE_MODE,
            subtitle = Constants.UI_PERFORMANCE_MODE_DESC,
            checked = performanceModeEnabled,
            onCheckedChange = onPerformanceModeChange
        )

        SettingsSwitchItem(
            icon = Icons.Default.Share,
            title = Constants.UI_SHARE_GAMES,
            subtitle = Constants.UI_SHARE_GAMES_DESC,
            checked = shareGamesEnabled,
            onCheckedChange = onShareGamesChange
        )
    }
} 