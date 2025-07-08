package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@Composable
fun ScreenshotGallery(
    modifier: Modifier = Modifier,
    screenshots: List<String>,
    imageQuality: ImageQuality,
    viewModel: ScreenshotGalleryViewModel = koinViewModel(),
    showEmptyState: Boolean = true,
) {
    val context = LocalContext.current
    if (screenshots.isNotEmpty()) {
        Column(modifier = modifier) {
            // Accessibility-Info
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = "Tippen Sie auf ein Bild für Vollbildansicht",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                screenshots.forEachIndexed { index, screenshotUrl ->
                    OptimizedScreenshotItem(
                        modifier = Modifier.size(width = 200.dp, height = 120.dp),
                        imageUrl = screenshotUrl,
                        onClick = {
                            ScreenshotGalleryActivity.start(
                                context,
                                screenshots,
                                index,
                                imageQuality
                            )
                        },
                        index = index
                    )
                }
            }
        }
    } else if (showEmptyState) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = androidx.compose.material.icons.Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Für dieses Spiel wurden keine Screenshots gefunden.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OptimizedScreenshotItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onClick: () -> Unit = {},
    index: Int = 0,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Screenshot $index",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onError = {
                // Optional: Hier könnte man Logging hinzufügen
            }
        )
        // Overlay für bessere UX
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f))
        ) {}
    }
}

