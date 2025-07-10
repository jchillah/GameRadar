package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class SyncFavoritesWithApiUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(rawgApi: RawgApi) {
        favoritesRepository.syncFavoritesWithApi(rawgApi)
    }
} 