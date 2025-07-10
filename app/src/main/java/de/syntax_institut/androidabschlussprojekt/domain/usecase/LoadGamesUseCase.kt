package de.syntax_institut.androidabschlussprojekt.domain.usecase

import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*
import androidx.compose.ui.res.stringResource

/**
 * UseCase zum Laden von Spielen mit optionalen Filtern und Paging.
 * Kapselt die Business-Logik für die Suche und das Paging von Spielen.
 */
class LoadGamesUseCase(
    private val gameRepository: GameRepository,
) {
    /**
     * Lädt Spiele mit den angegebenen Parametern.
     * @param query Suchbegriff
     * @param platforms Plattform-IDs als String (z.B. "1,2")
     * @param genres Genre-IDs als String (z.B. "4,5")
     * @param ordering Sortierung (z.B. "-rating")
     * @param rating Mindestbewertung
     * @return PagingData<Game> als Flow
     */
    operator fun invoke(
        query: String,
        platforms: String? = null,
        genres: String? = null,
        ordering: String? = null,
        rating: Float? = null,
    ): Flow<PagingData<Game>> = gameRepository.getPagedGames(
        query = query,
        platforms = platforms,
        genres = genres,
        ordering = ordering,
        rating = rating
    )
} 