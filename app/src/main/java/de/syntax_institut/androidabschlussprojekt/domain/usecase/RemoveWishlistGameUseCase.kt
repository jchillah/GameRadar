package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Entfernen eines Spiels von der Wunschliste.
 *
 * Kapselt die Logik für das Entfernen eines Spiels von der Wunschliste im Repository.
 */
class RemoveWishlistGameUseCase(private val repository: WishlistRepository) {
    /**
     * Entfernt das Spiel mit der angegebenen ID von der Wunschliste.
     * @param context Der Anwendungskontext
     * @param gameId Die ID des zu entfernenden Spiels
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, gameId: Int): Resource<Unit> =
        repository.removeFromWishlist(context, gameId)
}
