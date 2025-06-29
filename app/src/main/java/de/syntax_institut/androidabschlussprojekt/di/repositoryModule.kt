package de.syntax_institut.androidabschlussprojekt.di

import org.koin.dsl.module
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.data.repositories.FavoritesRepository
import de.syntax_institut.androidabschlussprojekt.data.local.GameDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.dao.FavoriteGameDao
import de.syntax_institut.androidabschlussprojekt.data.local.dao.GameCacheDao
import android.content.Context

/**
 * Modul für Repositories und Datenbank.
 */
val repositoryModule = module {
    // Room Database
    single { GameDatabase.getDatabase(get<Context>()) }
    
    // DAOs
    single<FavoriteGameDao> { get<GameDatabase>().favoriteGameDao() }
    single<GameCacheDao> { get<GameDatabase>().gameCacheDao() }
    
    // Repositories
    single {
        GameRepository(
            api = get(),
            gameCacheDao = get(),
            context = get()
        )
    }
    single { FavoritesRepository(get()) }
}