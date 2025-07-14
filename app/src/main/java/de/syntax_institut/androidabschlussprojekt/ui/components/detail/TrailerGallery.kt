package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun TrailerGallery(
    modifier: Modifier = Modifier,
    movies: List<Movie>,
    onTrailerClick: (Movie) -> Unit,
    showEmptyState: Boolean = false,
    gameHeaderImageUrl: String? = null, // NEU: Fallback für Thumbnails
) {
    Column(modifier = modifier) {
        if (movies.isEmpty() && showEmptyState) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.detail_no_trailers),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier =
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                movies.forEach { movie ->
                    val thumbnailUrl =
                        when {
                            !movie.preview.isNullOrBlank() -> movie.preview
                            !gameHeaderImageUrl.isNullOrBlank() -> gameHeaderImageUrl
                            else -> null
                        }
                    Box(
                        modifier =
                            Modifier
                                .size(width = 200.dp, height = 120.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onTrailerClick(movie) }
                    ) {
                        if (thumbnailUrl != null) {
                            // Zeige das Bild (z.B. mit Coil)
                            Image(
                                painter = rememberAsyncImagePainter(thumbnailUrl),
                                contentDescription = movie.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Platzhalter-Icon
                            Icon(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center),
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription =
                                    stringResource(R.string.game_headerimage_no_image),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.detail_trailer_play),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            text = movie.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
