package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.view.*
import androidx.media3.common.*
import androidx.media3.exoplayer.*
import androidx.media3.ui.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*

/**
 * Fullscreen Activity für den Trailer-Player.
 * Verwendet modernes Edge-to-Edge-API für echtes Fullscreen.
 */
class TrailerPlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    companion object {
        private const val EXTRA_VIDEO_URL = "video_url"
        private const val EXTRA_VIDEO_TITLE = "video_title"

        fun start(context: Context, videoUrl: String, videoTitle: String = "Trailer") {
            val intent = Intent(context, TrailerPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_VIDEO_TITLE, videoTitle)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-Edge aktivieren
        enableEdgeToEdge()

        // Systemleisten ausblenden für echtes Fullscreen
        hideSystemUi()

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Trailer"

        setContent {
            MyAppTheme {
                TrailerPlayerScreen(
                    videoUrl = videoUrl,
                    videoTitle = videoTitle,
                    onClose = {
                        releasePlayer()
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            try {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.release()
            } catch (_: Exception) {
                // Ignoriere Fehler beim Release
            }
        }
        player = null
    }

    @Composable
    private fun TrailerPlayerScreen(
        videoUrl: String,
        videoTitle: String,
        onClose: () -> Unit,
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ExoPlayer View
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = ExoPlayer.Builder(ctx).build().also { exoPlayer ->
                            player = exoPlayer
                            val mediaItem = MediaItem.fromUri(videoUrl)
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true

                            // Player Event Listener für bessere Kontrolle
                            exoPlayer.addListener(object : Player.Listener {
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    super.onPlaybackStateChanged(playbackState)
                                    if (playbackState == Player.STATE_ENDED) {
                                        // Video beendet - Activity schließen
                                        finish()
                                    }
                                }
                            })
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Schließen Button
            IconButton(
                onClick = {
                    releasePlayer()
                    onClose()
                },
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

            // Titel (optional)
            if (videoTitle.isNotBlank()) {
                Text(
                    text = videoTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )
            }
        }
    }
} 