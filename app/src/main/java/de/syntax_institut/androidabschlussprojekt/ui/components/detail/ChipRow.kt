package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.google.accompanist.flowlayout.*

@Composable
fun ChipRow(
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
            "Keine Daten vorhanden",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
} 