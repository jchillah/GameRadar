package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zur Prüfung, ob ein Spiel in den Favoriten ist.
 *
 * Kapselt die Logik für die Favoritenprüfung aus dem Repository.
 */
class IsFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Prüft, ob das Spiel mit der angegebenen ID als Favorit markiert ist.
     * @param gameId Die ID des Spiels
     * @return Boolean true, wenn Favorit, sonst false
     */
    suspend operator fun invoke(gameId: Int): Boolean {
        return favoritesRepository.isFavorite(gameId)
    }
} 