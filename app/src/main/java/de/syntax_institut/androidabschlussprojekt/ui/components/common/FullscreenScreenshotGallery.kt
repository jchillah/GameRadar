package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*

@Composable
fun FullscreenScreenshotGallery(
    screenshots: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(300),
        label = "scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(0.5f..5f)
                            scale = newScale

                            val maxOffset = (newScale - 1f) * 500f
                            offsetX = (offsetX + pan.x).coerceIn(-maxOffset..maxOffset)
                            offsetY = (offsetY + pan.y).coerceIn(-maxOffset..maxOffset)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2f
                                }
                            }
                        )
                    }
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(screenshots[currentIndex])
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Screenshot ${currentIndex + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 3.dp,
                                color = Color.White
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Fehler beim Laden",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                )
            }

            // Swipe-Gesten für Navigation (nur wenn nicht gezoomt)
            if (scale <= 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val x = dragAmount.x
                                val y = dragAmount.y

                                // Horizontale Swipe für Navigation
                                if (kotlin.math.abs(x) > kotlin.math.abs(y) && kotlin.math.abs(x) > 100f) {
                                    if (x > 0 && currentIndex > 0) {
                                        // Swipe nach rechts -> vorheriges Bild
                                        currentIndex--
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    } else if (x < 0 && currentIndex < screenshots.size - 1) {
                                        // Swipe nach links -> nächstes Bild
                                        currentIndex++
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }
                            }
                        }
                )
            }

            // Navigation Buttons
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Zurück Button (links)
                if (currentIndex > 0) {
                    IconButton(
                        onClick = {
                            currentIndex--
                            // Reset zoom und offset beim Bildwechsel
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                            contentDescription = "Vorheriges Bild",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Weiter Button (rechts)
                if (currentIndex < screenshots.size - 1) {
                    IconButton(
                        onClick = {
                            currentIndex++
                            // Reset zoom und offset beim Bildwechsel
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = "Nächstes Bild",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Schließen Button (oben rechts)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Schließen",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bildzähler (unten)
                Text(
                    text = "${currentIndex + 1} / ${screenshots.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }

            // Zoom Reset Button (nur sichtbar wenn gezoomt)
            if (scale != 1f || offsetX != 0f || offsetY != 0f) {
                FloatingActionButton(
                    onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text("Reset")
                }
            }
        }
    }
}