package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.google.accompanist.flowlayout.*

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
            "Keine Daten vorhanden",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipFlowRowPreview() {
    ChipFlowRow(items = listOf("Action", "Adventure", "RPG"))
} 