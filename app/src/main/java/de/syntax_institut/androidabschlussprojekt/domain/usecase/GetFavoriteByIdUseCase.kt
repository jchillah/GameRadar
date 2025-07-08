package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class GetFavoriteByIdUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(gameId: Int): Game? {
        return favoritesRepository.getFavoriteById(gameId)
    }
} 