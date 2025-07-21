package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt eine Liste von Chips in einer horizontal scrollbaren Reihe an.
 *
 * Verwendet horizontalScroll für horizontales Scrollen bei vielen Chips
 * und zeigt AssistChips für jede Liste von Strings an.
 * Zeigt eine "Keine Daten"-Nachricht an, wenn die Liste leer ist.
 *
 * @param items Liste der anzuzeigenden Strings
 * @param modifier Modifier für das Layout
 */
@Composable
fun ChipRow(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    if (items.isNotEmpty()) {
        Row(
            modifier = modifier
                .padding(vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    } else {
        Text(
            stringResource(R.string.detail_no_data),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipRowPreview() {
    ChipRow(
        items = listOf(
            stringResource(R.string.preview_tag_indie),
            stringResource(R.string.preview_tag_multiplayer),
            stringResource(R.string.preview_tag_open_world)
        )
    )
} 