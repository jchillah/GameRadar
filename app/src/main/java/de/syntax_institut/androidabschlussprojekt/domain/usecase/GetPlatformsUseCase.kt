package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase zum Abrufen aller verfügbaren Plattformen.
 *
 * Kapselt die Logik für das Laden der Plattformen aus dem Repository.
 */
class GetPlatformsUseCase(private val repository: GameRepository) {
    /**
     * Lädt alle Plattformen.
     * @return Resource<List<Platform>> mit allen Plattformen oder Fehler
     */
    suspend operator fun invoke(): Resource<List<Platform>> {
        return repository.getPlatforms()
    }
}
