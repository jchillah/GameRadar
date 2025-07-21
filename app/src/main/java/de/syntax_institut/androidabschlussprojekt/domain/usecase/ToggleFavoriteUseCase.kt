package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Umschalten des Favoritenstatus eines Spiels.
 *
 * Kapselt die Logik für das Hinzufügen oder Entfernen eines Spiels aus den Favoriten im Repository.
 */
class ToggleFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Schaltet den Favoritenstatus des angegebenen Spiels um.
     * @param context Der Anwendungskontext
     * @param game Das Spiel, dessen Favoritenstatus geändert werden soll
     * @return Resource<Boolean> true, wenn jetzt Favorit, false sonst
     */
    suspend operator fun invoke(context: Context, game: Game): Resource<Boolean> {
        return favoritesRepository.toggleFavorite(context, game)
    }
}
