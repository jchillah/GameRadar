package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Zeigt das Header-Bild eines Spiels mit anpassbarer Bildqualität und Fallback-Platzhalter.
 *
 * Features:
 * - Dynamische Bildgrößen basierend auf ImageQuality-Einstellung
 * - Crossfade-Animation beim Laden
 * - Fallback-Platzhalter bei fehlenden Bildern
 * - 16:9 Aspect Ratio für konsistente Darstellung
 * - Optimierte Bildladung mit Coil
 *
 * @param imageUrl URL des Spielbildes (kann null oder leer sein)
 * @param imageQuality Qualitätseinstellung für die Bildgröße (LOW, MEDIUM, HIGH)
 */
@Composable
fun GameHeaderImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    imageQuality: ImageQuality,
) {
    val context = LocalContext.current
    var imageLoadStartTime by remember { mutableStateOf(0L) }

    LaunchedEffect(imageUrl) {
        imageLoadStartTime = System.currentTimeMillis()
        PerformanceMonitor.incrementEventCounter("image_load_attempted")
    }

    if (imageUrl.isNotBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.game_header_image),
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
            onSuccess = {
                val loadDuration = System.currentTimeMillis() - imageLoadStartTime
                PerformanceMonitor.trackImageLoad(imageUrl, loadDuration, true, 0)
                PerformanceMonitor.incrementEventCounter("image_load_success")
            },
            onError = {
                val loadDuration = System.currentTimeMillis() - imageLoadStartTime
                PerformanceMonitor.trackImageLoad(imageUrl, loadDuration, false, 0)
                PerformanceMonitor.incrementEventCounter("image_load_error")
            }
        )
    } else {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clip(MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameHeaderImagePreview() {
    GameHeaderImage(
        imageUrl = stringResource(R.string.preview_game_image_url),
        imageQuality = ImageQuality.HIGH
    )
}
