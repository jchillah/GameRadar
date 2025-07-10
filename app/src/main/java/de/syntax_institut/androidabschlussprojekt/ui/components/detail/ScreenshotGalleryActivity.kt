package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.content.*
import android.content.pm.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
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
import androidx.core.view.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*
import de.syntax_institut.androidabschlussprojekt.data.Constants
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import kotlin.math.*
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Fullscreen Activity für die Screenshot-Galerie.
 * Verwendet modernes Edge-to-Edge-API für echtes Fullscreen.
 */
class ScreenshotGalleryActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_SCREENSHOTS = "screenshots"
        private const val EXTRA_START_INDEX = "start_index"
        private const val EXTRA_IMAGE_QUALITY = "image_quality"

        fun start(
            context: Context,
            screenshots: List<String>,
            startIndex: Int = 0,
            imageQuality: ImageQuality = ImageQuality.HIGH,
        ) {
            val intent = Intent(context, ScreenshotGalleryActivity::class.java).apply {
                putStringArrayListExtra(EXTRA_SCREENSHOTS, ArrayList(screenshots))
                putExtra(EXTRA_START_INDEX, startIndex)
                putExtra(EXTRA_IMAGE_QUALITY, imageQuality.title)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Orientierung freigeben für bessere Bildbetrachtung
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // Edge-to-Edge aktivieren
        enableEdgeToEdge()

        // Systemleisten ausblenden für echtes Fullscreen
        hideSystemUi()

        val screenshots = intent.getStringArrayListExtra(EXTRA_SCREENSHOTS) ?: arrayListOf()
        val startIndex = intent.getIntExtra(EXTRA_START_INDEX, 0)
        val imageQualityName = intent.getStringExtra(EXTRA_IMAGE_QUALITY) ?: ImageQuality.HIGH.title
        val imageQuality = ImageQuality.valueOf(imageQualityName)

        setContent {
            MyAppTheme {
                ScreenshotGalleryScreen(
                    screenshots = screenshots,
                    startIndex = startIndex,
                    imageQuality = imageQuality,
                    onClose = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @Composable
    private fun ScreenshotGalleryScreen(
        screenshots: List<String>,
        startIndex: Int,
        imageQuality: ImageQuality,
        onClose: () -> Unit,
    ) {
        var currentIndex by remember { mutableIntStateOf(startIndex) }
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        val context = LocalContext.current

        val animatedScale by animateFloatAsState(
            targetValue = scale,
            animationSpec = tween(300),
            label = "scale"
        )

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
                            val newOffsetX = (offsetX + pan.x).coerceIn(-maxOffset..maxOffset)
                            val newOffsetY = (offsetY + pan.y).coerceIn(-maxOffset..maxOffset)
                            offsetX = newOffsetX
                            offsetY = newOffsetY
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
                val size = when (imageQuality) {
                    ImageQuality.LOW -> Size(400, 240)
                    ImageQuality.MEDIUM -> Size(800, 480)
                    ImageQuality.HIGH -> Size.ORIGINAL
                }

                if (screenshots.isNotEmpty() && currentIndex < screenshots.size) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(screenshots[currentIndex])
                            .size(size)
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
                                        text = stringResource(R.string.error_load_data),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    )
                }
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
                                if (abs(x) > abs(y) && abs(x) > 100f) {
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
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Weiter Button (rechts)
                if (currentIndex < screenshots.size - 1) {
                    IconButton(
                        onClick = {
                            currentIndex++
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
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Schließen Button (oben rechts)
                IconButton(
                    onClick = onClose,
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

                // Bild-Indikator (unten)
                if (screenshots.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(screenshots.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (index == currentIndex) Color.White else Color.White.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}