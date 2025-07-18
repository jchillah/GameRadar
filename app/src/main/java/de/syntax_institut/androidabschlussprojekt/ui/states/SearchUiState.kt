package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.domain.models.*

data class SearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorMessageId: Int? = null,
    val selectedPlatforms: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val rating: Float = 0f,
    val ordering: String = "",
    val platforms: List<Platform> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val hasSearched: Boolean = false,
    val isOffline: Boolean = false,
    val cacheSize: Int = 0,
    val lastSyncTime: Long? = null,
    val isLoadingPlatforms: Boolean = false,
    val isLoadingGenres: Boolean = false,
    val platformsError: String? = null,
    val platformsErrorId: Int? = null,
    val genresError: String? = null,
    val genresErrorId: Int? = null,
    val isApplyingFilters: Boolean = false,
    val filterError: String? = null,
)
