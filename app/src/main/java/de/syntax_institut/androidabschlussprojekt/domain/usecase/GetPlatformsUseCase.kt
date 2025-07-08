package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class GetPlatformsUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(): Resource<List<Platform>> {
        return repository.getPlatforms()
    }
} 