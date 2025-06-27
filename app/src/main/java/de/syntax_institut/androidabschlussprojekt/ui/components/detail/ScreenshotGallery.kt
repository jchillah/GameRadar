package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

@Composable
fun ScreenshotGallery(screenshots: List<String>) {
    if (screenshots.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(screenshots.count()) { url ->
                GameHeaderImage(imageUrl = url.toString())
            }
        }
    } else {
        Text("Keine Daten vorhanden")
    }
}
