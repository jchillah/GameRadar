package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

class GetGameDetailUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(gameId: Int): Resource<Game> {
        return repository.getGameDetail(gameId)
    }
} 