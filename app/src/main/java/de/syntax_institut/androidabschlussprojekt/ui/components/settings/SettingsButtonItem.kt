package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Einstellungs-Item mit Button-Funktionalität für Aktionen.
 *
 * Features:
 * - Icon, Titel und Untertitel
 * - Klickbare Zeile mit Feedback
 * - Chevron-Right-Icon für Navigation
 * - Material3 Design-System
 * - Theme-adaptive Farben
 * - Accessibility-Unterstützung
 * - Callback für Klick-Aktionen
 *
 * @param icon Icon für die Einstellung
 * @param title Titel der Einstellung
 * @param subtitle Beschreibung der Einstellung
 * @param onClick Callback beim Klick auf das Item
 */
@Composable
fun SettingsButtonItem(
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
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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

@Preview(showBackground = true)
@Composable
fun SettingsButtonItemPreview() {
    SettingsButtonItem(
        icon = Icons.Filled.Info,
        title = stringResource(R.string.preview_settings_button_title),
        subtitle = stringResource(R.string.preview_settings_button_subtitle),
        onClick = {}
    )
} 