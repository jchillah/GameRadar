package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Sektion-Container für Einstellungsgruppen.
 *
 * Features:
 * - Sektionstitel in Primary-Farbe
 * - Material3 Card-Container für Inhalte
 * - Einheitliche Abstände zwischen Sektionen
 * - Responsive Layout
 * - Theme-adaptive Farben
 * - Accessibility-Unterstützung
 *
 * @param title Titel der Einstellungssektion
 * @param content Composable-Content für die Sektion
 */
@Composable
internal fun SettingsSection(
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

@Preview(showBackground = true)
@Composable
fun SettingsSectionPreview() {
    SettingsSection(title = stringResource(R.string.preview_settings_section_title)) {
        Text(stringResource(R.string.preview_settings_section_content))
    }
}