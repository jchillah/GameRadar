package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class ClearAllFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(): Resource<Unit> {
        return favoritesRepository.clearAllFavorites()
    }
} 