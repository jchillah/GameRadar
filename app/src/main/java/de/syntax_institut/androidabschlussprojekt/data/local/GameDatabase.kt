package de.syntax_institut.androidabschlussprojekt.data.local

import android.content.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*

/**
 * Room-Datenbank für die App. Enthält Tabellen für Favoriten, Wunschliste, Game-Cache und
 * Detail-Cache. Bietet Zugriff auf alle DAOs und verwaltet Migrationen.
 *
 * @constructor Erstellt eine Instanz der GameDatabase
 * @property favoriteGameDao DAO für Favoriten-Spiele
 * @property gameCacheDao DAO für gecachte Spiele
 * @property gameDetailCacheDao DAO für gecachte Spieldetails
 * @property wishlistGameDao DAO für die Wunschliste
 */
@Database(
    entities =
        [
            FavoriteGameEntity::class,
            GameCacheEntity::class,
            GameDetailCacheEntity::class,
            WishlistGameEntity::class],
    version = 3,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {

    /** DAO für Favoriten-Spiele. */
    abstract fun favoriteGameDao(): FavoriteGameDao

    /** DAO für gecachte Spiele. */
    abstract fun gameCacheDao(): GameCacheDao

    /** DAO für gecachte Spieldetails. */
    abstract fun gameDetailCacheDao(): GameDetailCacheDao

    /** DAO für die Wunschliste. */
    abstract fun wishlistGameDao(): WishlistGameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        /**
         * Gibt die Singleton-Instanz der Datenbank zurück. Erstellt die Datenbank, falls sie noch
         * nicht existiert.
         *
         * @param context Anwendungskontext
         * @return Instanz der GameDatabase
         */
        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            GameDatabase::class.java,
                            Constants.DATABASE_NAME
                        )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build()
                    INSTANCE = instance
                    instance
                }
        }

        /**
         * Migration von Version 1 zu Version 2: Fügt die Tabelle wishlist_games hinzu und das
         * movies Feld zur game_cache Tabelle.
         */
        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS wishlist_games (
                            id INTEGER PRIMARY KEY NOT NULL,
                            slug TEXT NOT NULL,
                            title TEXT NOT NULL,
                            releaseDate TEXT,
                            imageUrl TEXT,
                            rating REAL NOT NULL,
                            description TEXT,
                            metacritic INTEGER,
                            website TEXT,
                            esrbRating TEXT,
                            screenshots TEXT NOT NULL,
                            movies TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        "ALTER TABLE " +
                                Constants.GAME_CACHE_TABLE +
                                " ADD COLUMN movies TEXT DEFAULT '[]'"
                    )
                }
            }

        /** Migration von Version 2 zu Version 3: Fügt die game_detail_cache Tabelle hinzu. */
        private val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS game_detail_cache (
                        id INTEGER PRIMARY KEY NOT NULL,
                        slug TEXT NOT NULL,
                        title TEXT NOT NULL,
                        releaseDate TEXT,
                        imageUrl TEXT,
                        rating REAL NOT NULL,
                        description TEXT,
                        metacritic INTEGER,
                        website TEXT,
                        esrbRating TEXT,
                        genres TEXT NOT NULL,
                        platforms TEXT NOT NULL,
                        developers TEXT NOT NULL,
                        publishers TEXT NOT NULL,
                        tags TEXT NOT NULL,
                        screenshots TEXT NOT NULL,
                        stores TEXT NOT NULL,
                        playtime INTEGER,
                        movies TEXT NOT NULL,
                        detailCachedAt INTEGER NOT NULL
                    )
                """.trimIndent()
                    )
                }
            }

        /**
         * Löscht die Datenbank und erstellt sie neu. Nützlich bei Migrationsproblemen.
         *
         * @param context Anwendungskontext
         */
        fun clearDatabase(context: Context) {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
                context.applicationContext.deleteDatabase(Constants.DATABASE_NAME)
            }
        }
    }
}
