package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class GetCacheSizeUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(): Int {
        return repository.getCacheSize()
    }
} 