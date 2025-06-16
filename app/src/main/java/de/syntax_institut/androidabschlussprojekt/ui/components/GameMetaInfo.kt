package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameMetaInfo(
    title: String,
    releaseDate: String,
    rating: Double
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(releaseDate)
    Spacer(modifier = Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Text("Rating: $rating")
}
