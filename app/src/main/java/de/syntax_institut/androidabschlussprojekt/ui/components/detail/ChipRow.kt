package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*

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
            "Keine Daten vorhanden",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipRowPreview() {
    ChipRow(items = listOf("Indie", "Multiplayer", "Open World"))
} 