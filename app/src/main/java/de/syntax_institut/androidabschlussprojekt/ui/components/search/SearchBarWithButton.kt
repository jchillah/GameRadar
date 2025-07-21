package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

/**
 * Suchleiste mit Button und erweiterten Funktionen.
 *
 * Features:
 * - Textfeld mit Platzhalter
 * - Such-Button für manuelle Suche
 * - Loading-Indikator während der Suche
 * - Clear-Button für Textfeld-Inhalt
 * - Responsive Layout mit Gewichtung
 * - Material3 Design-System
 * - Accessibility-Unterstützung
 *
 * @param modifier Modifier für das Layout
 * @param searchText Aktueller Suchtext als TextFieldValue
 * @param onTextChange Callback bei Textänderungen
 * @param onSearchClick Callback für Such-Button-Klicks
 * @param isLoading Gibt an, ob eine Suche läuft
 * @param onClear Optionaler Callback zum Löschen des Suchtexts
 */
@Composable
fun SearchBarWithButton(
    modifier: Modifier = Modifier,
    searchText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean = false,
    onClear: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            label = {
                Text(
                    stringResource(R.string.search_placeholder),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        Loading(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (searchText.text.isNotBlank() && onClear != null) {
                        IconButton(
                            onClick = { onClear() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onSearchClick,
            enabled = !isLoading,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.height(56.dp)
        ) {
            if (isLoading) {
                Loading(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.search_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
