package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Hinzufügen eines Spiels zur Wunschliste.
 *
 * Kapselt die Logik für das Hinzufügen eines Spiels zur Wunschliste im Repository.
 */
class AddWishlistGameUseCase(private val repository: WishlistRepository) {
    /**
     * Fügt ein Spiel zur Wunschliste hinzu.
     * @param context Der Anwendungskontext
     * @param game Das hinzuzufügende Spiel
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, game: Game): Resource<Unit> =
        repository.addToWishlist(context, game)
}
