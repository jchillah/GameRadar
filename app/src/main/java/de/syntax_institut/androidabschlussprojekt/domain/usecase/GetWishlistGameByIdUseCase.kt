package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class GetWishlistGameByIdUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(gameId: Int): Game? = repository.getWishlistGameById(gameId)
}
