package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class GetWishlistCountUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(): Int = repository.getWishlistCount()
}
