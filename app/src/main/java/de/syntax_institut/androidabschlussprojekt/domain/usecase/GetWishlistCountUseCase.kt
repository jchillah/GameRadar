package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Abrufen der Anzahl der Spiele auf der Wunschliste.
 *
 * Kapselt die Logik für das Zählen der Wunschlistenspiele aus dem Repository.
 */
class GetWishlistCountUseCase(private val repository: WishlistRepository) {
    /**
     * Gibt die Anzahl der Spiele auf der Wunschliste zurück.
     * @return Int Anzahl der Wunschlistenspiele
     */
    suspend operator fun invoke(): Int = repository.getWishlistCount()
}
