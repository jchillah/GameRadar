package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.domain.models.Platform
import de.syntax_institut.androidabschlussprojekt.domain.models.Genre

data class SearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPlatforms: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val rating: Float = 0f,
    val platforms: List<Platform> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val ordering: String = "",
    val hasSearched: Boolean = false,
    // Offline-Funktionalität
    val isOffline: Boolean = false,
    val cacheSize: Int = 0,
    val lastSyncTime: Long? = null,
    val isLoadingPlatforms: Boolean = false,
    val isLoadingGenres: Boolean = false,
    val platformsError: String? = null,
    val genresError: String? = null,
    val isApplyingFilters: Boolean = false,
    val filterError: String? = null
)
