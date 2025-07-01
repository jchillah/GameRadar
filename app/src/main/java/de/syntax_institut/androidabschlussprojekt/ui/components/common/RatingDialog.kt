package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
fun RatingDialog(
    currentRating: Float,
    onRatingChanged: (Float) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRating by remember { mutableFloatStateOf(currentRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Spiel bewerten") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Wie bewertest du dieses Spiel?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        val starValue = index + 1f
                        IconButton(
                            onClick = { selectedRating = starValue }
                        ) {
                            Icon(
                                imageVector = if (selectedRating >= starValue)
                                    Icons.Filled.Star
                                else
                                    Icons.Filled.StarBorder,
                                contentDescription = "$starValue Sterne",
                                tint = if (selectedRating >= starValue)
                                    Color(0xFFFFD700)
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${selectedRating.toInt()} von 5 Sternen",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onRatingChanged(selectedRating) }
            ) {
                Text("Bewerten")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}