package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Abrufen eines bestimmten Spiels aus der Wunschliste anhand der ID.
 *
 * Kapselt die Logik für das Laden eines Wunschlistenspiels aus dem Repository.
 */
class GetWishlistGameByIdUseCase(private val repository: WishlistRepository) {
    /**
     * Gibt das Spiel mit der angegebenen ID aus der Wunschliste zurück (oder null, falls nicht vorhanden).
     * @param gameId Die ID des gesuchten Spiels
     * @return Game? Das gefundene Spiel oder null
     */
    suspend operator fun invoke(gameId: Int): Game? = repository.getWishlistGameById(gameId)
}
