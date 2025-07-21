package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.google.accompanist.flowlayout.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt eine Liste von Chips in einem Flow-Layout an.
 *
 * Verwendet FlowRow für automatisches Umbruch-Verhalten und
 * zeigt AssistChips für jede Liste von Strings an.
 * Zeigt eine "Keine Daten"-Nachricht an, wenn die Liste leer ist.
 *
 * @param items Liste der anzuzeigenden Strings
 * @param modifier Modifier für das Layout
 */
@Composable
fun ChipFlowRow(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    if (items.isNotEmpty()) {
        FlowRow(
            mainAxisSpacing = 4.dp,
            crossAxisSpacing = 4.dp,
            modifier = modifier.padding(vertical = 4.dp)
        ) {
            items.forEach {
                AssistChip(
                    onClick = {},
                    label = { Text(it) },
                    modifier = Modifier.padding(end = 4.dp))
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
fun ChipFlowRowPreview() {
    ChipFlowRow(
        items = listOf(
            stringResource(R.string.preview_genre_action),
            stringResource(R.string.preview_genre_adventure),
            stringResource(R.string.preview_genre_rpg)
        )
    )
} 