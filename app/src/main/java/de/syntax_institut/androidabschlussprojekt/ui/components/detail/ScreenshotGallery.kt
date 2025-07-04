package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.util.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import coil3.size.Size
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun ScreenshotGallery(screenshots: List<String>, imageQuality: ImageQuality) {
    LaunchedEffect(screenshots) {
        Log.d("ScreenshotGallery", "Screenshots erhalten: ${screenshots.size}")
        screenshots.forEachIndexed { index, url ->
            Log.d("ScreenshotGallery", "Screenshot $index: $url")
        }
    }

    var showFullscreenGallery by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    
    if (screenshots.isNotEmpty()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Screenshots (${screenshots.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                // Accessibility-Info
                Text(
                    text = "Tippen Sie auf ein Bild für Vollbildansicht",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                itemsIndexed(screenshots) { index, screenshotUrl ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(
                            initialOffsetX = { it * (index + 1) },
                            animationSpec = tween(durationMillis = 300 + (index * 100))
                        ) + fadeIn(animationSpec = tween(durationMillis = 300))
                    ) {
                        OptimizedScreenshotItem(
                            imageUrl = screenshotUrl,
                            modifier = Modifier.size(width = 200.dp, height = 120.dp),
                            onClick = {
                                selectedImageIndex = index
                                showFullscreenGallery = true
                            },
                            index = index,
                            imageQuality = imageQuality
                        )
                    }
                }
            }
        }

        // Vollbild-Galerie Dialog mit Animation
        AnimatedVisibility(
            visible = showFullscreenGallery,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300)
            ),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(300)
            )
        ) {
            FullscreenScreenshotGallery(
                screenshots = screenshots,
                initialIndex = selectedImageIndex,
                onDismiss = { showFullscreenGallery = false },
                imageQuality = imageQuality
            )
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Keine Screenshots verfügbar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OptimizedScreenshotItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    index: Int = 0,
    imageQuality: ImageQuality,
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .clickable { onClick() }
            .semantics {
                contentDescription = "Screenshot ${index + 1}. Tippen Sie für Vollbildansicht."
            }
            .graphicsLayer {
                scaleX = if (isPressed) 0.95f else 1f
                scaleY = if (isPressed) 0.95f else 1f
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val size = when (imageQuality) {
                ImageQuality.LOW -> Size(200, 120)
                ImageQuality.MEDIUM -> Size(400, 240)
                ImageQuality.HIGH -> Size.ORIGINAL
            }
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(size)
                    .crossfade(true)
                    .build(),
                contentDescription = "Screenshot ${index + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.BrokenImage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Fehler beim Laden",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            // Overlay für bessere UX
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

