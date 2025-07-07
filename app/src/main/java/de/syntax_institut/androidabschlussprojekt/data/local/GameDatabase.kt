package de.syntax_institut.androidabschlussprojekt.data.local

import android.content.*
import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*

/**
 * Room Database für die App.
 * Enthält Tabellen für Favoriten und Offline-Cache-Funktionalität.
 */
@Database(
    entities = [FavoriteGameEntity::class, GameCacheEntity::class],
    version = 4,
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
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 