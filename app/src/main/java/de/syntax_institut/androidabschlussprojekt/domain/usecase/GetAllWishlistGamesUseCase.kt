package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

class GetAllWishlistGamesUseCase(private val repository: WishlistRepository) {
    operator fun invoke(): Flow<List<Game>> = repository.getAllWishlistGames()
}
