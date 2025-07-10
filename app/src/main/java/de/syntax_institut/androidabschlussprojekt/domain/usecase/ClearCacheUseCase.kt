package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class ClearCacheUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() {
        repository.clearCache()
    }
} 