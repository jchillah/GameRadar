package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

class GetAllFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    operator fun invoke(): Flow<List<Game>> {
        return favoritesRepository.getAllFavorites()
    }
} 