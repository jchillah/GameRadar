package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class ClearAllWishlistGamesUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(): Resource<Unit> = repository.clearAllWishlistGames()
}
