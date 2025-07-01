package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import de.syntax_institut.androidabschlussprojekt.domain.models.Platform
import de.syntax_institut.androidabschlussprojekt.domain.models.Genre
import de.syntax_institut.androidabschlussprojekt.ui.components.common.OfflineBanner

@Composable
fun FilterBottomSheet(
    platforms: List<Platform>,
    genres: List<Genre>,
    selectedPlatforms: List<String>,
    selectedGenres: List<String>,
    rating: Float,
    ordering: String,
    isLoadingPlatforms: Boolean = false,
    isLoadingGenres: Boolean = false,
    platformsError: String? = null,
    genresError: String? = null,
    isOffline: Boolean = false,
    onOrderingChange: (String) -> Unit = {},
    onFilterChange: (List<String>, List<String>, Float) -> Unit,
    onRetryPlatforms: () -> Unit = {},
    onRetryGenres: () -> Unit = {},
    onClearCache: () -> Unit = {}
) {
    var selectedPlatformState by remember { mutableStateOf(selectedPlatforms.toSet()) }
    var selectedGenreState by remember { mutableStateOf(selectedGenres.toSet()) }
    var ratingState by remember { mutableFloatStateOf(rating) }
    var orderingState by remember { mutableStateOf(ordering) }
    
    // Dropdown-Zustände
    var platformsExpanded by remember { mutableStateOf(false) }
    var genresExpanded by remember { mutableStateOf(false) }
    var orderingExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Offline-Banner
        OfflineBanner(isOffline = isOffline)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Plattformen Dropdown
        Text("Plattformen", style = MaterialTheme.typography.titleMedium)
        
        if (isLoadingPlatforms) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                strokeWidth = 2.dp
            )
        } else if (platformsError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fehler beim Laden der Plattformen:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = platformsError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRetryPlatforms,
                        enabled = !isOffline
                    ) {
                        Text("Erneut versuchen")
                    }
                }
            }
        } else {
            Box {
                OutlinedButton(
                    onClick = { platformsExpanded = true },
                    enabled = !isOffline,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedPlatformState.isEmpty()) "Plattformen auswählen" 
                        else "${selectedPlatformState.size} Plattform(en) ausgewählt"
                    )
                }
                DropdownMenu(
                    expanded = platformsExpanded,
                    onDismissRequest = { platformsExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    platforms.forEach { platform ->
                        val isSelected = selectedPlatformState.contains(platform.id.toString())
                        DropdownMenuItem(
                            text = { 
                                Row {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(platform.name)
                                }
                            },
                            onClick = {
                                selectedPlatformState = if (isSelected) {
                                    selectedPlatformState - platform.id.toString()
                                } else {
                                    selectedPlatformState + platform.id.toString()
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Genres Dropdown
        Text("Genres", style = MaterialTheme.typography.titleMedium)
        
        if (isLoadingGenres) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                strokeWidth = 2.dp
            )
        } else if (genresError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fehler beim Laden der Genres:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = genresError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRetryGenres,
                        enabled = !isOffline
                    ) {
                        Text("Erneut versuchen")
                    }
                }
            }
        } else {
            Box {
                OutlinedButton(
                    onClick = { genresExpanded = true },
                    enabled = !isOffline,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedGenreState.isEmpty()) "Genres auswählen" 
                        else "${selectedGenreState.size} Genre(s) ausgewählt"
                    )
                }
                DropdownMenu(
                    expanded = genresExpanded,
                    onDismissRequest = { genresExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    genres.forEach { genre ->
                        val isSelected = selectedGenreState.contains(genre.id.toString())
                        DropdownMenuItem(
                            text = { 
                                Row {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(genre.name)
                                }
                            },
                            onClick = {
                                selectedGenreState = if (isSelected) {
                                    selectedGenreState - genre.id.toString()
                                } else {
                                    selectedGenreState + genre.id.toString()
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mindestbewertung
        Text("Mindestbewertung: ${ratingState.roundToInt()}", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = ratingState,
            onValueChange = { ratingState = it },
            valueRange = 0f..5f,
            steps = 4,
            enabled = !isOffline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sortierung Dropdown
        Text("Sortierung", style = MaterialTheme.typography.titleMedium)
        val orderings = listOf(
            "-rating" to "Bewertung (absteigend)",
            "rating" to "Bewertung (aufsteigend)",
            "-released" to "Release (neueste)",
            "released" to "Release (älteste)",
            "name" to "Name (A-Z)",
            "-name" to "Name (Z-A)"
        )
        Box {
            OutlinedButton(
                onClick = { orderingExpanded = true },
                enabled = !isOffline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(orderings.find { it.first == orderingState }?.second ?: "Sortierung wählen")
            }
            DropdownMenu(
                expanded = orderingExpanded, 
                onDismissRequest = { orderingExpanded = false }
            ) {
                orderings.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            orderingState = value
                            onOrderingChange(value)
                            orderingExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cache-Verwaltung (nur im Offline-Modus)
        if (isOffline) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Offline-Modus",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Nur gecachte Daten verfügbar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onClearCache,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cache leeren")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter anwenden Button
        Button(
            onClick = {
                onFilterChange(
                    selectedPlatformState.toList(),
                    selectedGenreState.toList(),
                    ratingState
                )
            },
            enabled = !isOffline || (platforms.isNotEmpty() && genres.isNotEmpty()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filter anwenden")
        }
    }
}
