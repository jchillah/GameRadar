package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase für das Umschalten eines Spiels in der Wunschliste.
 *
 * Kapselt die Logik für das Hinzufügen oder Entfernen eines Spiels aus der Wunschliste.
 */
class ToggleWishlistGameUseCase(private val repository: WishlistRepository) {
    /**
     * Schaltet ein Spiel in der Wunschliste um (hinzufügen wenn nicht vorhanden, entfernen wenn vorhanden).
     * @param context Der Anwendungskontext
     * @param game Das umzuschaltende Spiel
     * @return Resource<Boolean> mit dem neuen Status (true = in Wunschliste, false = nicht in Wunschliste)
     */
    suspend operator fun invoke(context: Context, game: Game): Resource<Boolean> =
        repository.toggleWishlist(context, game)
}
