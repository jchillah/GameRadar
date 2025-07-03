package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*

@Composable
fun ActiveFiltersRow(
    modifier: Modifier = Modifier,
    selectedPlatformIds: List<String>,
    selectedGenreIds: List<String>,
    allPlatforms: List<Platform>,
    allGenres: List<Genre>,
    rating: Float,
    ordering: String,
    onRemovePlatform: (String) -> Unit,
    onRemoveGenre: (String) -> Unit,
    onRemoveRating: () -> Unit,
    onRemoveOrdering: () -> Unit,
    onClearAll: () -> Unit,
) {
    val hasFilters =
        selectedPlatformIds.isNotEmpty() || selectedGenreIds.isNotEmpty() || rating > 0f || ordering.isNotBlank()
    if (!hasFilters) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Plattformen
        selectedPlatformIds.forEach { id ->
            val name = allPlatforms.find { it.id.toString() == id }?.name ?: id
            FilterChip(
                selected = true,
                label = { Text(name) },
                onClick = { onRemovePlatform(id) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Plattform-Filter entfernen"
                    )
                }
            )
        }
        // Genres
        selectedGenreIds.forEach { id ->
            val name = allGenres.find { it.id.toString() == id }?.name ?: id
            FilterChip(
                selected = true,
                label = { Text(name) },
                onClick = { onRemoveGenre(id) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Genre-Filter entfernen"
                    )
                }
            )
        }
        // Mindestbewertung
        if (rating > 0f) {
            FilterChip(
                selected = true,
                label = { Text("Bewertung ≥ ${rating.toInt()}") },
                onClick = onRemoveRating,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Bewertungs-Filter entfernen"
                    )
                }
            )
        }
        // Sortierung
        if (ordering.isNotBlank()) {
            val orderingLabel = when (ordering) {
                "-rating" -> "Bewertung (absteigend)"
                "rating" -> "Bewertung (aufsteigend)"
                "-released" -> "Release (neueste)"
                "released" -> "Release (älteste)"
                "name" -> "Name (A-Z)"
                "-name" -> "Name (Z-A)"
                else -> ordering
            }
            FilterChip(
                selected = true,
                label = { Text(orderingLabel) },
                onClick = onRemoveOrdering,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Sortierung entfernen"
                    )
                }
            )
        }
        // Alle Filter zurücksetzen
        FilterChip(
            selected = true,
            label = { Text("Alle Filter zurücksetzen") },
            onClick = onClearAll,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Alle Filter entfernen"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                labelColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
} 