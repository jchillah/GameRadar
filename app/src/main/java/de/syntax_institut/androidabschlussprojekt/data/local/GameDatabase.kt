package de.syntax_institut.androidabschlussprojekt.data.local

import android.content.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*

/**
 * Room Database für die App.
 * Enthält Tabellen für Favoriten und Offline-Cache-Funktionalität.
 */
@Database(
    entities = [FavoriteGameEntity::class, GameCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    
    abstract fun favoriteGameDao(): FavoriteGameDao
    abstract fun gameCacheDao(): GameCacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null
        
        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    .addMigrations(MIGRATION_1_2)
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
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE game_cache ADD COLUMN movies TEXT DEFAULT '[]'")
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
                context.applicationContext.deleteDatabase("game_database")
            }
        }
    }
} 