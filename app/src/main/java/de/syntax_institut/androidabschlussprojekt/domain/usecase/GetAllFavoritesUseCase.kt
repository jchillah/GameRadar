package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

/**
 * UseCase zum Abrufen aller gespeicherten Favoriten.
 *
 * Kapselt die Logik für das Laden der Favoritenliste aus dem Repository.
 */
class GetAllFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Gibt einen Flow mit allen gespeicherten Favoriten zurück.
     * @return Flow<List<Game>> mit allen Favoriten
     */
    operator fun invoke(): Flow<List<Game>> {
        return favoritesRepository.getAllFavorites()
    }
}
