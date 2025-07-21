package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R


/**
 * Dialog für die Benutzerbewertung eines Spiels mit Stern-System.
 *
 * Zeigt 5 Sterne zur Bewertung an, die interaktiv ausgewählt werden können.
 * Die Sterne sind golden gefärbt, wenn sie ausgewählt sind.
 *
 * @param currentRating Aktuelle Bewertung (0.0 - 5.0)
 * @param onRatingChanged Callback bei Bewertungsänderung
 * @param onDismiss Callback beim Schließen des Dialogs
 */
@Composable
fun RatingDialog(
    currentRating: Float,
    onRatingChanged: (Float) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRating by remember { mutableFloatStateOf(currentRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rating_dialog_title)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.rating_dialog_question))
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
                                contentDescription = stringResource(
                                    R.string.rating_dialog_star_content_description,
                                    starValue.toInt()
                                ),
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
                    text = stringResource(
                        R.string.rating_dialog_rating_of_five,
                        selectedRating.toInt()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onRatingChanged(selectedRating) }
            ) {
                Text(stringResource(R.string.rating_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.rating_dialog_cancel))
            }
        }
    )
}