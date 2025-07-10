package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class RemoveFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(gameId: Int): Resource<Unit> {
        return favoritesRepository.removeFavorite(gameId)
    }
} 