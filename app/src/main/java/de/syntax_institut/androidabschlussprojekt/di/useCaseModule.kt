package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import org.koin.dsl.*

val useCaseModule = module {
    single { LoadGamesUseCase(get()) }
    // Weitere UseCases hier ergänzen
} 