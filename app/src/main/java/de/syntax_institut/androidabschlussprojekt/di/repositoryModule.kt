package de.syntax_institut.androidabschlussprojekt.di

import org.koin.dsl.module
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.data.repositories.FavoritesRepository
import de.syntax_institut.androidabschlussprojekt.data.local.GameDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.dao.FavoriteGameDao

/**
 * Modul für Repositories und Datenbank.
 */
val repositoryModule = module {
    // Room Database
    single { GameDatabase.getDatabase(get()) }
    
    // DAOs
    single<FavoriteGameDao> { get<GameDatabase>().favoriteGameDao() }
    
    // Repositories
    single { GameRepository(get()) }
    single { FavoritesRepository(get()) }
}