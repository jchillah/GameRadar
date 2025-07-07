package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.*
import androidx.core.net.*
import androidx.media3.common.*
import androidx.media3.exoplayer.*
import androidx.media3.ui.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Zeigt einen Video-Player für einen Trailer (Movie) an.
 * Nutzt ExoPlayer (Media3). Für Fullscreen geeignet, Systemleisten-Steuerung erfolgt im Dialog.
 * @param movie Der abzuspielende Trailer
 * @param modifier Modifier für Größe/Platzierung
 */

@Composable
fun TrailerPlayerView(
    movie: Movie,
    modifier: Modifier = Modifier,
    viewModel: TrailerPlayerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val playWhenReady by viewModel.playWhenReady.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val url = movie.urlMax ?: movie.url480 ?: movie.preview
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(movie) {
        val mediaItem = url?.takeIf { it.isNotBlank() }?.let { MediaItem.fromUri(it.toUri()) }
        if (mediaItem != null) {
            exoPlayer.setMediaItem(mediaItem)
        }
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.seekTo(playbackPosition)
        exoPlayer.prepare()
        onDispose {
            viewModel.savePlayerState(
                position = exoPlayer.currentPosition,
                playWhenReady = exoPlayer.playWhenReady
            )
            exoPlayer.release()
        }
    }
    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier.fillMaxSize()
    )
} 