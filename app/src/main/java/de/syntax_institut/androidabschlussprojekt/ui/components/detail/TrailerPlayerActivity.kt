package de.syntax_institut.androidabschlussprojekt.ui.components.detail

// Kein expliziter R-Import nötig, stringResource(R.string.action_close) reicht
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
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.view.*
import androidx.media3.common.*
import androidx.media3.exoplayer.*
import androidx.media3.ui.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.theme.*

/**
 * Vollbild-Activity für das Abspielen von Trailern mit ExoPlayer.
 *
 * Diese Activity zeigt einen Trailer im Fullscreen-Modus an und blendet Systemleisten aus.
 *
 * @constructor Erstellt eine neue TrailerPlayerActivity.
 */
class TrailerPlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    companion object {
        private const val EXTRA_VIDEO_URL = "video_url"
        private const val EXTRA_VIDEO_TITLE = "video_title"

        /**
         * Startet die TrailerPlayerActivity mit der angegebenen Video-URL und optionalem Titel.
         *
         * @param context Context zum Starten der Activity
         * @param videoUrl Die URL des abzuspielenden Videos
         * @param videoTitle Der Titel des Videos (optional)
         */
        fun start(context: Context, videoUrl: String, videoTitle: String = "Trailer") {
            val intent =
                Intent(context, TrailerPlayerActivity::class.java).apply {
                    putExtra(EXTRA_VIDEO_URL, videoUrl)
                    putExtra(EXTRA_VIDEO_TITLE, videoTitle)
                }
            context.startActivity(intent)
        }
    }

    /** Initialisiert die Activity und setzt das Fullscreen-Layout. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    /** Blendet die Systemleisten für echtes Fullscreen aus. */
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /** Gibt den ExoPlayer frei und räumt Ressourcen auf. */
    private fun releasePlayer() {
        player?.let { exoPlayer ->
            try {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.release()
            } catch (_: Exception) {
                // Fehler beim Release ignorieren
            }
        }
        player = null
    }

    /**
     * Zeigt den Trailer-Player im Fullscreen an.
     *
     * @param videoUrl Die URL des Videos
     * @param videoTitle Der Titel des Videos
     * @param onClose Callback zum Schließen des Players
     */
    @Composable
    private fun TrailerPlayerScreen(
        videoUrl: String,
        videoTitle: String,
        onClose: () -> Unit,
    ) {
        var playbackError by remember { mutableStateOf<String?>(null) }
        var retryKey by remember { mutableIntStateOf(0) }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            if (playbackError == null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player =
                                ExoPlayer.Builder(ctx).build().also { exoPlayer ->
                                    player = exoPlayer
                                    val mediaItem = MediaItem.fromUri(videoUrl)
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                    exoPlayer.playWhenReady = true
                                    exoPlayer.addListener(
                                        object : Player.Listener {
                                            override fun onPlaybackStateChanged(
                                                playbackState: Int,
                                            ) {
                                                super.onPlaybackStateChanged(
                                                    playbackState
                                                )
                                                if (playbackState == Player.STATE_ENDED
                                                ) {
                                                    finish()
                                                }
                                            }

                                            override fun onPlayerError(
                                                error: PlaybackException,
                                            ) {
                                                // Prüfe auf Netzwerkfehler
                                                if (error.errorCode ==
                                                    PlaybackException
                                                        .ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                                    error.cause is
                                                            java.net.UnknownHostException ||
                                                    error.cause?.message
                                                        ?.contains(
                                                            "Unable to resolve host"
                                                        ) == true
                                                ) {
                                                    playbackError =
                                                        "Trailer kann nicht geladen werden. Bitte überprüfe deine Internetverbindung."
                                                } else {
                                                    playbackError =
                                                        "Fehler beim Abspielen des Trailers: ${error.localizedMessage ?: "Unbekannter Fehler"}"
                                                }
                                            }
                                        }
                                    )
                                }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fehleranzeige mit Retry
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = playbackError ?: "Fehler beim Abspielen des Trailers.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            playbackError = null
                            retryKey++
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Erneut versuchen")
                    }
                }
            }
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
                    contentDescription = stringResource(R.string.action_close),
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
