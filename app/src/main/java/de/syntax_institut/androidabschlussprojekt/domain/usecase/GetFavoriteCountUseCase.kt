package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Zählen der gespeicherten Favoriten.
 *
 * Kapselt die Logik für das Zählen der Favoriten im Repository.
 */
class GetFavoriteCountUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Gibt die Anzahl der gespeicherten Favoriten zurück.
     * @return Int mit der Anzahl der Favoriten
     */
    suspend operator fun invoke(): Int {
        return favoritesRepository.getFavoriteCount()
    }
}
