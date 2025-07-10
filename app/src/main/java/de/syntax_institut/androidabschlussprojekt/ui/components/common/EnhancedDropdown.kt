package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.models.*

/**
 * Erweiterte Dropdown-Komponente mit Icons, Trennlinien und verbesserter UX.
 * Folgt Material Design Guidelines für Dropdown-Menüs.
 */
@Composable
fun EnhancedDropdown(
    modifier: Modifier = Modifier,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    options: List<DropdownOption>,
    enabled: Boolean = true,
    placeholder: String = stringResource(R.string.dropdown_placeholder),
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { if (enabled) expanded = true },
            enabled = enabled,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedValue.ifEmpty { placeholder },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown_open),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            modifier = Modifier.width(IntrinsicSize.Min),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, option ->
                // Trennlinie vor bestimmten Optionen
                if (option.showDividerBefore && index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (option.value == selectedValue) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = option.icon?.let { icon ->
                        {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (option.value == selectedValue)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    trailingIcon = if (option.value == selectedValue) {
                        {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.dropdown_selected),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else null,
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EnhancedDropdownPreview() {
    val options = listOf(
        DropdownOption("de", stringResource(R.string.language_german), Icons.Default.Language),
        DropdownOption("en", stringResource(R.string.language_english), Icons.Default.Language)
    )

    EnhancedDropdown(
        selectedValue = stringResource(R.string.language_german),
        onValueChange = {},
        options = options
    )
} 