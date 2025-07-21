package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

/**
 * UseCase für die Suche in der Wunschliste.
 *
 * Kapselt die Logik für die Suche nach Spielen in der Wunschliste aus dem Repository.
 */
class SearchWishlistGamesUseCase(private val repository: WishlistRepository) {
    /**
     * Sucht nach Spielen in der Wunschliste basierend auf einer Suchanfrage.
     * @param query Die Suchanfrage
     * @return Flow<List<Game>> mit den gefundenen Spielen
     */
    operator fun invoke(query: String): Flow<List<Game>> = repository.searchWishlistGames(query)
}
