package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zur Prüfung, ob ein Spiel auf der Wunschliste ist.
 *
 * Kapselt die Logik für die Wunschlistenprüfung aus dem Repository.
 */
class IsInWishlistUseCase(private val repository: WishlistRepository) {
    /**
     * Prüft, ob das Spiel mit der angegebenen ID auf der Wunschliste steht.
     * @param gameId Die ID des Spiels
     * @return Boolean true, wenn auf der Wunschliste, sonst false
     */
    suspend operator fun invoke(gameId: Int): Boolean = repository.isInWishlist(gameId)
}
