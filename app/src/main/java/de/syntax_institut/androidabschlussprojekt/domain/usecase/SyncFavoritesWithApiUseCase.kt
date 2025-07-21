package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase für die Synchronisation der Favoriten mit der API.
 *
 * Kapselt die Logik für die Synchronisation der lokalen Favoriten mit den aktuellen Daten aus der API.
 */
class SyncFavoritesWithApiUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Synchronisiert die Favoriten mit den aktuellen Daten aus der API.
     * @param rawgApi Die RAWG API-Instanz für den Datenabruf
     */
    suspend operator fun invoke(rawgApi: RawgApi) {
        favoritesRepository.syncFavoritesWithApi(rawgApi)
    }
} 