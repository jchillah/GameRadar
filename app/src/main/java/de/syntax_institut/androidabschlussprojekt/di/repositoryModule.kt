package de.syntax_institut.androidabschlussprojekt.di

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import org.koin.dsl.*

/**
 * DI-Modul für Datenbank, DAOs und Repositories.
 *
 * Stellt die Room-Datenbank, alle DAOs und die wichtigsten Repository-Implementierungen bereit.
 */
val repositoryModule = module {
    /**
     * Singleton-Instanz der Room-Datenbank.
     */
    single { GameDatabase.getDatabase(get<Context>()) }

    /**
     * DAOs für Favoriten, Cache, Spieldetails und Wunschliste.
     */
    single<FavoriteGameDao> { get<GameDatabase>().favoriteGameDao() }
    single<GameCacheDao> { get<GameDatabase>().gameCacheDao() }
    single<GameDetailCacheDao> { get<GameDatabase>().gameDetailCacheDao() }
    single<WishlistGameDao> { get<GameDatabase>().wishlistGameDao() }

    /**
     * Repositories für Spiele, Favoriten, Wunschliste und Einstellungen.
     */
    single {
        GameRepository(
            api = get(),
            gameCacheDao = get(),
            context = get(),
            gameDetailCacheDao = get()
        )
    }
    single { FavoritesRepository(get(), get()) }
    single { WishlistRepository(get()) }
    single { SettingsRepository(get<Context>()) }
}
