package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Leeren der gesamten Wunschliste.
 *
 * Kapselt die Logik für das Löschen aller Spiele von der Wunschliste aus dem Repository.
 */
class ClearAllWishlistGamesUseCase(private val repository: WishlistRepository) {
    /**
     * Löscht alle Spiele von der Wunschliste.
     * @param context Der Anwendungskontext
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context): Resource<Unit> =
        repository.clearAllWishlistGames(context)
}
