package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R
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
            .horizontalScroll(rememberScrollState())
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
                        contentDescription = stringResource(R.string.filter_remove_platform),
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
                        contentDescription = stringResource(R.string.filter_remove_genre),
                    )
                }
            )
        }
        // Mindestbewertung
        if (rating > 0f) {
            FilterChip(
                selected = true,
                label = { Text(stringResource(R.string.filter_rating_label, rating.toInt())) },
                onClick = onRemoveRating,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.filter_remove_rating),
                    )
                }
            )
        }
        // Sortierung
        if (ordering.isNotBlank()) {
            val orderingLabel = when (ordering) {
                "-rating" -> stringResource(R.string.filter_ordering_rating_desc)
                "rating" -> stringResource(R.string.filter_ordering_rating_asc)
                "-released" -> stringResource(R.string.filter_ordering_release_desc)
                "released" -> stringResource(R.string.filter_ordering_release_asc)
                "name" -> stringResource(R.string.filter_ordering_name_asc)
                "-name" -> stringResource(R.string.filter_ordering_name_desc)
                else -> ordering
            }
            FilterChip(
                selected = true,
                label = { Text(orderingLabel) },
                onClick = onRemoveOrdering,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.filter_remove_ordering),
                    )
                }
            )
        }
        // Alle Filter zurücksetzen
        FilterChip(
            selected = true,
            label = { Text(stringResource(R.string.filter_reset_all)) },
            onClick = onClearAll,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.filter_remove_all),
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                labelColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
} 