package de.syntax_institut.androidabschlussprojekt.di

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import org.koin.dsl.*

/** Modul für Repositories und Datenbank. */
val repositoryModule = module {
    // Room Database
    single { GameDatabase.getDatabase(get<Context>()) }

    // DAOs
    single<FavoriteGameDao> { get<GameDatabase>().favoriteGameDao() }
    single<GameCacheDao> { get<GameDatabase>().gameCacheDao() }
    single<GameDetailCacheDao> { get<GameDatabase>().gameDetailCacheDao() }
    single<WishlistGameDao> { get<GameDatabase>().wishlistGameDao() }

    // Repositories
    single {
        GameRepository(
            api = get(),
            gameCacheDao = get(),
            favoriteGameDao = get(),
            context = get(),
            gameDetailCacheDao = get()
        )
    }
    single { FavoritesRepository(get(), get()) }
    single { WishlistRepository(get()) }
    single { SettingsRepository(get<Context>()) }
}
