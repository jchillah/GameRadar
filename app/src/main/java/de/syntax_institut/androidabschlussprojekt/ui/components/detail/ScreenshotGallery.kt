package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun ScreenshotGallery(screenshots: List<String>) {
    val context = LocalContext.current
    
    // Debug-Logging
    LaunchedEffect(screenshots) {
        Log.d("ScreenshotGallery", "Screenshots erhalten: ${screenshots.size}")
        screenshots.forEachIndexed { index, url ->
            Log.d("ScreenshotGallery", "Screenshot $index: $url")
        }
    }
    
    if (screenshots.isNotEmpty()) {
        Column {
            Text(
                text = "Screenshots (${screenshots.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(screenshots.size) { index ->
                    val screenshotUrl = screenshots[index]
                    ScreenshotItem(
                        imageUrl = screenshotUrl,
                        modifier = Modifier.size(width = 200.dp, height = 120.dp)
                    )
                }
            }
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
                Text(
                    text = "Keine Screenshots verfügbar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ScreenshotItem(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                error = painterResource(R.drawable.ic_broken_image)
            ),
            contentDescription = "Screenshot",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
