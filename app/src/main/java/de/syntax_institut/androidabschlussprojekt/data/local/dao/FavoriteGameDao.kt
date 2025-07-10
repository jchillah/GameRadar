package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Favoriten-Spiele Operationen.
 */
@Dao
interface FavoriteGameDao {
    
    /**
     * Alle Favoriten als Flow zurückgeben (für reaktive UI-Updates).
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>
    
    /**
     * Prüfen ob ein Spiel als Favorit gespeichert ist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId)")
    suspend fun isFavorite(gameId: Int): Boolean
    
    /**
     * Ein spezifisches Favoriten-Spiel abrufen.
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId")
    suspend fun getFavoriteById(gameId: Int): FavoriteGameEntity?
    
    /**
     * Favorit hinzufügen oder aktualisieren.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteGameEntity)
    
    /**
     * Favorit entfernen.
     */
    @Query("DELETE FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId")
    suspend fun removeFavorite(gameId: Int)
    
    /**
     * Alle Favoriten löschen.
     */
    @Query("DELETE FROM " + Constants.FAVORITE_GAME_TABLE)
    suspend fun clearAllFavorites()
    
    /**
     * Anzahl der Favoriten.
     */
    @Query("SELECT COUNT(*) FROM " + Constants.FAVORITE_GAME_TABLE)
    suspend fun getFavoriteCount(): Int
    
    /**
     * Favoriten nach Titel suchen.
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE title LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteGameEntity>>
} 