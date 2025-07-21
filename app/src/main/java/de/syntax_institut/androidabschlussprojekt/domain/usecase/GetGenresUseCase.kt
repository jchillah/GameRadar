package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Abrufen aller verfügbaren Genres.
 *
 * Kapselt die Logik für das Laden der Genres aus dem Repository.
 */
class GetGenresUseCase(private val repository: GameRepository) {
    /**
     * Lädt alle Genres.
     * @return Resource<List<Genre>> mit allen Genres oder Fehler
     */
    suspend operator fun invoke(): Resource<List<Genre>> {
        return repository.getGenres()
    }
}
