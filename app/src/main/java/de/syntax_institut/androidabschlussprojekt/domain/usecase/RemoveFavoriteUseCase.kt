package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class RemoveFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(context: Context, gameId: Int): Resource<Unit> {
        return favoritesRepository.removeFavorite(context, gameId)
    }
}
