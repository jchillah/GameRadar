package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class IsInWishlistUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(gameId: Int): Boolean = repository.isInWishlist(gameId)
}
