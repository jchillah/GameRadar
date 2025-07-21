package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Abrufen der Spieldetails für eine bestimmte Game-ID.
 *
 * Kapselt die Logik für das Laden der Detaildaten eines Spiels aus dem Repository.
 */
class GetGameDetailUseCase(private val repository: GameRepository) {
    /**
     * Lädt die Spieldetails für die angegebene Game-ID.
     * @param gameId Die ID des Spiels
     * @return Resource<Game> mit den Spieldetails oder Fehler
     */
    suspend operator fun invoke(gameId: Int): Resource<Game> {
        return repository.getGameDetail(gameId)
    }
}
