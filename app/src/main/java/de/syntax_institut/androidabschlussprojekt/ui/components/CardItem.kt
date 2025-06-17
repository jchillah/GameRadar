package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.*

@Composable
fun CardItem(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors()
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    CardItem {
        Text("Preview Card Content")
    }
}
