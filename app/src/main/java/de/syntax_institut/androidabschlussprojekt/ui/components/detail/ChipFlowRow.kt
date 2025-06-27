package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun ChipFlowRow(
    items: List<String>,
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        label,
        style = MaterialTheme.typography.titleMedium
    )
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