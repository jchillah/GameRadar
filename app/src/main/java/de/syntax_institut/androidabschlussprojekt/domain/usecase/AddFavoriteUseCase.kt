package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Hinzufügen eines Spiels zu den Favoriten.
 *
 * Kapselt die Logik für das Hinzufügen eines Spiels zu den Favoriten im Repository.
 */
class AddFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Fügt ein Spiel zu den Favoriten hinzu.
     * @param context Der Anwendungskontext
     * @param game Das hinzuzufügende Spiel
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, game: Game): Resource<Unit> {
        return favoritesRepository.addFavorite(context, game)
    }
}
