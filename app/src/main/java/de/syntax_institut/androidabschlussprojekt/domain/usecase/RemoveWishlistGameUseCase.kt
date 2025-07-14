package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class RemoveWishlistGameUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(gameId: Int): Resource<Unit> = repository.removeFromWishlist(gameId)
}
