package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class RemoveWishlistGameUseCase(private val repository: WishlistRepository) {
    suspend operator fun invoke(context: Context, gameId: Int): Resource<Unit> =
        repository.removeFromWishlist(context, gameId)
}
