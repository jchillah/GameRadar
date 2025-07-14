package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class ClearAllFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(context: Context): Resource<Unit> {
        return favoritesRepository.clearAllFavorites(context)
    }
}
