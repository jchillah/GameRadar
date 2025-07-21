package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

/**
 * UseCase für die Suche in den Favoriten.
 *
 * Kapselt die Logik für die Suche nach Spielen in den Favoriten aus dem Repository.
 */
class SearchFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Sucht nach Spielen in den Favoriten basierend auf einer Suchanfrage.
     * @param query Die Suchanfrage
     * @return Flow<List<Game>> mit den gefundenen Spielen
     */
    operator fun invoke(query: String): Flow<List<Game>> {
        return favoritesRepository.searchFavorites(query)
    }
} 