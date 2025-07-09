package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import de.syntax_institut.androidabschlussprojekt.data.*

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
            title = Constants.UI_DARK_MODE,
            subtitle = Constants.UI_DARK_MODE_DESC,
            checked = darkModeEnabled,
            onCheckedChange = onDarkModeChange
        )
    }
} 