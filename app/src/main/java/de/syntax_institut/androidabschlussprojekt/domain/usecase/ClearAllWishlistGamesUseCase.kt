package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class ClearAllWishlistGamesUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(context: Context): Resource<Unit> =
        repository.clearAllWishlistGames(context)
}
