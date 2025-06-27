package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import de.syntax_institut.androidabschlussprojekt.domain.model.Platform
import de.syntax_institut.androidabschlussprojekt.domain.model.Genre

@Composable
fun FilterBottomSheet(
    platforms: List<Platform>,
    genres: List<Genre>,
    selectedPlatforms: List<String>,
    selectedGenres: List<String>,
    rating: Float,
    ordering: String,
    onOrderingChange: (String) -> Unit = {},
    onFilterChange: (List<String>, List<String>, Float) -> Unit
) {
    var selectedPlatformState by remember { mutableStateOf(selectedPlatforms.toSet()) }
    var selectedGenreState by remember { mutableStateOf(selectedGenres.toSet()) }
    var ratingState by remember { mutableFloatStateOf(rating) }
    var orderingState by remember { mutableStateOf(ordering) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Plattformen", style = MaterialTheme.typography.titleMedium)
        FlowRow {
            platforms.forEach { platform ->
                val isSelected = selectedPlatformState.contains(platform.id.toString())
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedPlatformState = if (isSelected) {
                            selectedPlatformState - platform.id.toString()
                        } else {
                            selectedPlatformState + platform.id.toString()
                        }
                    },
                    label = { Text(platform.name) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Genres", style = MaterialTheme.typography.titleMedium)
        FlowRow {
            genres.forEach { genre ->
                val isSelected = selectedGenreState.contains(genre.id.toString())
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedGenreState = if (isSelected) {
                            selectedGenreState - genre.id.toString()
                        } else {
                            selectedGenreState + genre.id.toString()
                        }
                    },
                    label = { Text(genre.name) },
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

        Text("Sortierung", style = MaterialTheme.typography.titleMedium)
        val orderings = listOf(
            "-rating" to "Bewertung (absteigend)",
            "rating" to "Bewertung (aufsteigend)",
            "-released" to "Release (neueste)",
            "released" to "Release (älteste)",
            "name" to "Name (A-Z)",
            "-name" to "Name (Z-A)"
        )
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(orderings.find { it.first == orderingState }?.second ?: "Sortierung wählen")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                orderings.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            orderingState = value
                            onOrderingChange(value)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onFilterChange(
                selectedPlatformState.toList(),
                selectedGenreState.toList(),
                ratingState
            )
        }) {
            Text("Filter anwenden")
        }
    }
}
