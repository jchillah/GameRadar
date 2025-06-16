package de.syntax_institut.androidabschlussprojekt.di

import org.koin.dsl.module
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository

/**
 * Modul für Repositories.
 */
val repositoryModule = module {
    single { GameRepository(get()) }
}