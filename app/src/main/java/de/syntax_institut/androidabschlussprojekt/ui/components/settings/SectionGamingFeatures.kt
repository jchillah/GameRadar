package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import de.syntax_institut.androidabschlussprojekt.R

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
            title = stringResource(R.string.gaming_mode),
            subtitle = stringResource(R.string.gaming_mode_description),
            checked = gamingModeEnabled,
            onCheckedChange = onGamingModeChange
        )

        SettingsSwitchItem(
            icon = Icons.Default.Speed,
            title = stringResource(R.string.performance_mode),
            subtitle = stringResource(R.string.performance_mode_description),
            checked = performanceModeEnabled,
            onCheckedChange = onPerformanceModeChange
        )

        SettingsSwitchItem(
            icon = Icons.Default.Share,
            title = stringResource(R.string.share_games),
            subtitle = stringResource(R.string.share_games_description),
            checked = shareGamesEnabled,
            onCheckedChange = onShareGamesChange
        )
    }
} 