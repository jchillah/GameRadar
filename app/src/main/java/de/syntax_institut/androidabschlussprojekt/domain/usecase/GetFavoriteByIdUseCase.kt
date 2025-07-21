package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Abrufen eines Favoriten anhand der Game-ID.
 *
 * Kapselt die Logik für das Laden eines bestimmten Favoriten aus dem Repository.
 */
class GetFavoriteByIdUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Gibt das Spiel mit der angegebenen ID zurück, falls es als Favorit gespeichert ist.
     * @param gameId Die ID des Spiels
     * @return Game? Das gefundene Spiel oder null
     */
    suspend operator fun invoke(gameId: Int): Game? {
        return favoritesRepository.getFavoriteById(gameId)
    }
}
