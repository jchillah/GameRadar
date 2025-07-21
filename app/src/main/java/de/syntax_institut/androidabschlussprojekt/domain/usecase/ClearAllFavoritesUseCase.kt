package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Leeren aller Favoriten.
 *
 * Kapselt die Logik für das Löschen aller Favoriten aus dem Repository.
 */
class ClearAllFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    /**
     * Löscht alle Favoriten.
     * @param context Der Anwendungskontext
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context): Resource<Unit> {
        return favoritesRepository.clearAllFavorites(context)
    }
}
