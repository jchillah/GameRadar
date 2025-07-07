package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun GameHeaderImage(imageUrl: String?, imageQuality: ImageQuality) {
    val context = LocalContext.current
    val size = when (imageQuality) {
        ImageQuality.LOW -> Size(400, 225)
        ImageQuality.MEDIUM -> Size(800, 450)
        ImageQuality.HIGH -> Size.ORIGINAL
    }
    if (imageUrl.isNullOrBlank()) {
        // Platzhalter anzeigen, wenn kein Bild vorhanden ist
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Kein Bild verfügbar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
        }
    } else {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(size)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameHeaderImagePreview() {
    GameHeaderImage(
        imageUrl = "https://media.rawg.io/media/games/4fb/4fb548e4816c84d1d70f1a228fb167cc.jpg",
        imageQuality = ImageQuality.HIGH
    )
}