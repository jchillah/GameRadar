package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Entfernen eines Spiels aus den Favoriten.
 *
 * Kapselt die Logik für das Entfernen eines Spiels aus den Favoriten im Repository.
 */
class RemoveFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Entfernt das Spiel mit der angegebenen ID aus den Favoriten.
     * @param context Der Anwendungskontext
     * @param gameId Die ID des zu entfernenden Spiels
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, gameId: Int): Resource<Unit> {
        return favoritesRepository.removeFavorite(context, gameId)
    }
}
