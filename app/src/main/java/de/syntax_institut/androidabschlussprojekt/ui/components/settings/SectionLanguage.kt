package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

@Composable
fun SectionLanguage(
    modifier: Modifier = Modifier,
    language: String,
    onLanguageChange: (String) -> Unit,
) {
    // Verwende LocaleManager für verfügbare Sprachen
    val availableLanguages = remember { LocaleManager.getAvailableLanguagesForUI() }

    // Erstelle Dropdown-Optionen mit korrekten Werten
    val languageOptions =
        remember(availableLanguages) {
            availableLanguages.map { (code, name) ->
                DropdownOption(code, name, Icons.Default.Language)
            }
        }

    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_language),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.app_language_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Finde die aktuelle Sprache
        val currentLanguageOption =
            languageOptions.find { it.value == language } ?: languageOptions.first()

        EnhancedDropdown(
            modifier = Modifier.padding(top = 8.dp),
            selectedValue = currentLanguageOption.label,
            onValueChange = { selectedLabel ->
                // Finde den Sprachcode basierend auf dem ausgewählten Label
                val selectedCode =
                    languageOptions.find { it.label == selectedLabel }?.value
                selectedCode?.let { onLanguageChange(it) }
            },
            options = languageOptions
        )
    }
}
