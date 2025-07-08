package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class IsFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(gameId: Int): Boolean {
        return favoritesRepository.isFavorite(gameId)
    }
} 