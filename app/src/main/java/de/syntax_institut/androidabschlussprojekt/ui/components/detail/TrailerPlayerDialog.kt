package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.syntax_institut.androidabschlussprojekt.data.local.models.Movie

@Composable
fun TrailerPlayerDialog(
    movie: Movie,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TrailerPlayerView(
                movie = movie,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Schließen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
} 

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrailerPlayerDialogPreview() {
    TrailerPlayerDialog(
        movie = Movie(
            id = 1,
            name = "Gameplay Trailer",
            preview = "https://media.rawg.io/media/movies/preview/4fb/4fb548e4816c84d1d70f1a228fb167cc.jpg",
            url480 = "https://media.rawg.io/media/movies/480/4fb/4fb548e4816c84d1d70f1a228fb167cc.mp4",
            urlMax = "https://media.rawg.io/media/movies/max/4fb/4fb548e4816c84d1d70f1a228fb167cc.mp4"
        )
    ) {
        // Dismiss Callback
    }
}

/**
 * Der Player läuft bereits im Fullscreen-Dialog (blockiert Hintergrund, nimmt gesamte Breite/Höhe ein).
 *
 * Für einen "echten" Fullscreen-Toggle (System UI ausblenden, immersive mode):
 * - In Compose gibt es aktuell keinen 100% nativen Weg, aber du kannst z.B. mit Accompanist SystemUiController oder Activity-WindowFlags arbeiten.
 * - Alternativ: Player in ein separates Activity/Fragment auslagern und dort System UI ausblenden (siehe ExoPlayer/Media3 Doku).
 * - Compose 1.6+ und Media3 bieten immer mehr Compose-native Lösungen (z.B. PlayerSurface, SystemBarsController etc.).
 */