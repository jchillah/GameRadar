package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class AddFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(game: Game): Resource<Unit> {
        return favoritesRepository.addFavorite(game)
    }
} 