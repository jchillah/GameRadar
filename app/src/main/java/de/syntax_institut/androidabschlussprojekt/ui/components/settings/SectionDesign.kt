package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Design-Sektion für die Einstellungen.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
@Composable
fun SectionDesign(
    modifier: Modifier = Modifier,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Column(modifier = modifier) {
        SettingsSwitchItem(
            icon = Icons.Default.DarkMode,
            title = stringResource(R.string.ui_dark_mode),
            subtitle = stringResource(R.string.ui_dark_mode_desc),
            checked = darkModeEnabled,
            onCheckedChange = onDarkModeChange
        )
    }
} 