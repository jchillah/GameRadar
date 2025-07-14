package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class ToggleFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(context: Context, game: Game): Resource<Boolean> {
        return favoritesRepository.toggleFavorite(context, game)
    }
}
