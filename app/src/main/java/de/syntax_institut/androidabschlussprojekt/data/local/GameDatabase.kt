package de.syntax_institut.androidabschlussprojekt.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import de.syntax_institut.androidabschlussprojekt.data.local.dao.FavoriteGameDao
import de.syntax_institut.androidabschlussprojekt.data.local.entities.FavoriteGameEntity

/**
 * Room Database für die App.
 * Enthält Tabellen für Favoriten und zukünftige Offline-Cache-Funktionalität.
 */
@Database(
    entities = [FavoriteGameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    
    abstract fun favoriteGameDao(): FavoriteGameDao
    
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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 