package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun FilterBottomSheet(
    allPlatforms: List<Pair<Int, String>>, // List of available platforms (ID, Name)
    allGenres: List<Pair<Int, String>>, // List of available genres (ID, Name)
    // Using Set<Int> to store selected IDs
 selectedPlatforms: Set<Int>,
    selectedGenres: Set<Int>,
    rating: Float,
    onFilterChange: (List<String>, List<String>, Float) -> Unit
) {
    var selectedPlatformState by remember { mutableStateOf(selectedPlatforms.toSet()) }
    var selectedGenreState by remember { mutableStateOf(selectedGenres.toSet()) }
    var ratingState by remember { mutableFloatStateOf(rating) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Plattformen", style = MaterialTheme.typography.titleMedium)
        FlowRow {
            allPlatforms.forEach { (platformId, platformName) ->
                val isSelected = selectedPlatformState.contains(platformId)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedPlatformState = if (isSelected) {
 selectedPlatformState - platformId
                        } else {
                            selectedPlatformState + platform
                        }
                    },
                    label = { Text(platform) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Genres", style = MaterialTheme.typography.titleMedium)
        FlowRow {
            allGenres.forEach { (genreId, genreName) ->
                val isSelected = selectedGenreState.contains(genreId)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedGenreState = if (isSelected) {
 selectedGenreState - genreId
                        } else {
                            selectedGenreState + genre
                        }
                    },
                    label = { Text(genre) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Mindestbewertung: ${ratingState.roundToInt()}", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = ratingState,
            onValueChange = { ratingState = it },
            valueRange = 0f..5f,
            steps = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onFilterChange(
                selectedPlatformState.toList(), // Pass selected platform IDs
                selectedGenreState.toList(), // Pass selected genre IDs
                ratingState
            )
        }) {
            Text("Filter anwenden")
        }
    }
}
