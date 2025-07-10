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
    val languageOptions = listOf(
        DropdownOption(
            "system",
            stringResource(R.string.system_language),
            Icons.Default.Language
        ),
        DropdownOption(
            "de",
            stringResource(R.string.language_german),
            Icons.Default.Language
        ),
        DropdownOption(
            "en",
            stringResource(R.string.language_english),
            Icons.Default.Language
        )
    )

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
        EnhancedDropdown(
            modifier = Modifier.padding(top = 8.dp),
            selectedValue = languageOptions.firstOrNull { it.value == language }?.label ?: languageOptions[0].label,
            onValueChange = onLanguageChange,
            options = languageOptions
        )
    }
} 