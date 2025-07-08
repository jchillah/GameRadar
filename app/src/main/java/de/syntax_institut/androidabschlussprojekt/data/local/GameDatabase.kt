package de.syntax_institut.androidabschlussprojekt.data.local

import android.content.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*

/**
 * Room Database für die App.
 * Enthält Tabellen für Favoriten und Offline-Cache-Funktionalität.
 */
@Database(
    entities = [FavoriteGameEntity::class, GameCacheEntity::class, GameDetailCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    
    abstract fun favoriteGameDao(): FavoriteGameDao
    abstract fun gameCacheDao(): GameCacheDao
    abstract fun gameDetailCacheDao(): GameDetailCacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null
        
        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
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
         * Migration von Version 1 zu Version 2:
         * Fügt das movies Feld zur game_cache Tabelle hinzu
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + Constants.GAME_CACHE_TABLE + " ADD COLUMN movies TEXT DEFAULT '[]'")
            }
        }

        /**
         * Migration von Version 2 zu Version 3:
         * Fügt die game_detail_cache Tabelle hinzu
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS " + Constants.GAME_DETAIL_CACHE_TABLE + " (
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
         * Löscht die Datenbank und erstellt sie neu.
         * Nützlich bei Migrationsproblemen.
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