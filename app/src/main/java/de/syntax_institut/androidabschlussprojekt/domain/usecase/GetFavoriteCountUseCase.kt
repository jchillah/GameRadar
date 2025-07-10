package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class GetFavoriteCountUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(): Int {
        return favoritesRepository.getFavoriteCount()
    }
} 