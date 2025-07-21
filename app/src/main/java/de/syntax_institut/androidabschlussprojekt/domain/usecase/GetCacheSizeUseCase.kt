package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Abrufen der aktuellen Cache-Größe.
 *
 * Kapselt die Logik für das Ermitteln der Cache-Größe aus dem Repository.
 */
class GetCacheSizeUseCase(private val repository: GameRepository) {
    /**
     * Gibt die aktuelle Cache-Größe zurück.
     * @return Int mit der Anzahl der gecachten Spiele
     */
    suspend operator fun invoke(): Int {
        return repository.getCacheSize()
    }
}
