package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun TrailerGallery(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onTrailerClick: (Movie) -> Unit,
) {
    if (movies.isEmpty()) return
    Column(modifier = modifier) {
        Text(
            text = "Trailer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                TrailerCard(movie = movie, onClick = { onTrailerClick(movie) })
            }
        }
    }
}

@Composable
private fun TrailerCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(200.dp)
            .aspectRatio(16f / 9f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(movie.preview)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Abspielen",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                text = movie.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
} 