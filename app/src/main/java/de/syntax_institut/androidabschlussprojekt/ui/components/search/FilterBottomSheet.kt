package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import kotlin.math.*

/**
 * BottomSheet-Komponente für erweiterte Filteroptionen in der Spielesuche.
 *
 * Features:
 * - Plattform- und Genre-Auswahl mit Multi-Select
 * - Mindestbewertung per Slider
 * - Sortierungsauswahl
 * - Fehler- und Ladezustände für Plattformen/Genres
 * - Offline-Unterstützung und Cache-Management
 * - "Filter anwenden"-Button
 *
 * @param platforms Liste aller verfügbaren Plattformen
 * @param genres Liste aller verfügbaren Genres
 * @param selectedPlatforms IDs der ausgewählten Plattformen
 * @param selectedGenres IDs der ausgewählten Genres
 * @param rating Mindestbewertung (0-5)
 * @param ordering Sortierreihenfolge
 * @param isLoadingPlatforms Gibt an, ob Plattformen geladen werden
 * @param isLoadingGenres Gibt an, ob Genres geladen werden
 * @param platformsErrorId Fehler-String-Ressourcen-ID für Plattformen
 * @param genresErrorId Fehler-String-Ressourcen-ID für Genres
 * @param isOffline Gibt an, ob die App offline ist
 * @param onOrderingChange Callback bei Änderung der Sortierung
 * @param onFilterChange Callback beim Anwenden der Filter
 * @param onRetryPlatforms Callback zum erneuten Laden der Plattformen
 * @param onRetryGenres Callback zum erneuten Laden der Genres
 * @param onClearCache Callback zum Leeren des Caches (nur offline)
 */
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
    platformsErrorId: Int? = null,
    genresErrorId: Int? = null,
    isOffline: Boolean = false,
    onOrderingChange: (String) -> Unit = {},
    onFilterChange: (List<String>, List<String>, Float) -> Unit,
    onRetryPlatforms: () -> Unit = {},
    onRetryGenres: () -> Unit = {},
    onClearCache: () -> Unit = {},
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
        Text(stringResource(R.string.game_platforms), style = MaterialTheme.typography.titleMedium)

        if (isLoadingPlatforms) {
            Loading(modifier = Modifier.padding(16.dp))
        } else if (platformsErrorId != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(platformsErrorId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetryPlatforms, enabled = !isOffline) {
                        Text(stringResource(R.string.action_retry))
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
                        if (selectedPlatformState.isEmpty())
                            stringResource(R.string.filter_select_platforms)
                        else
                            stringResource(
                                R.string.filter_selected_platforms,
                                selectedPlatformState.size
                            )
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
                                    Checkbox(checked = isSelected, onCheckedChange = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(platform.name)
                                }
                            },
                            onClick = {
                                selectedPlatformState =
                                    if (isSelected) {
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
        Text(stringResource(R.string.game_genres), style = MaterialTheme.typography.titleMedium)

        if (isLoadingGenres) {
            Loading(modifier = Modifier.padding(16.dp))
        } else if (genresErrorId != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(genresErrorId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetryGenres, enabled = !isOffline) {
                        Text(stringResource(R.string.action_retry))
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
                        if (selectedGenreState.isEmpty())
                            stringResource(R.string.filter_select_genres)
                        else
                            stringResource(
                                R.string.filter_selected_genres,
                                selectedGenreState.size
                            )
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
                                    Checkbox(checked = isSelected, onCheckedChange = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(genre.name)
                                }
                            },
                            onClick = {
                                selectedGenreState =
                                    if (isSelected) {
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
        Text(
            stringResource(R.string.filter_min_rating, ratingState.roundToInt()),
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = ratingState,
            onValueChange = { ratingState = it },
            valueRange = 0f..5f,
            steps = 4,
            enabled = !isOffline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sortierung Dropdown
        Text(stringResource(R.string.filter_sorting), style = MaterialTheme.typography.titleMedium)
        val orderings =
            listOf(
                "-rating" to stringResource(R.string.filter_ordering_rating_desc),
                "rating" to stringResource(R.string.filter_ordering_rating_asc),
                "-released" to stringResource(R.string.filter_ordering_release_desc),
                "released" to stringResource(R.string.filter_ordering_release_asc),
                "name" to stringResource(R.string.filter_ordering_name_asc),
                "-name" to stringResource(R.string.filter_ordering_name_desc)
            )
        Box {
            OutlinedButton(
                onClick = { orderingExpanded = true },
                enabled = !isOffline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    orderings.find { it.first == orderingState }?.second
                        ?: stringResource(R.string.filter_select_sorting),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            DropdownMenu(
                expanded = orderingExpanded,
                onDismissRequest = { orderingExpanded = false },
                modifier = Modifier.width(300.dp)
            ) {
                orderings.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight =
                                    if (value == orderingState) FontWeight.SemiBold
                                    else FontWeight.Normal
                            )
                        },
                        leadingIcon =
                            if (value == orderingState) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
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
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.offline_mode),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = stringResource(R.string.offline_no_data),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onClearCache,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                    ) { Text(stringResource(R.string.clear_cache)) }
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
        ) { Text(stringResource(R.string.filter_apply)) }
    }
}
