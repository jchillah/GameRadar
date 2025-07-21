package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

/**
 * UseCase zum Leeren des Spiele-Caches.
 *
 * Kapselt die Logik für das Löschen des lokalen Spiele-Caches aus dem Repository.
 */
class ClearCacheUseCase(private val repository: GameRepository) {
    /** Löscht den Spiele-Cache. */
    suspend operator fun invoke() {
        repository.clearCache()
    }
}
