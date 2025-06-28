package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.FavoriteGameEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO für Favoriten-Spiele Operationen.
 */
@Dao
interface FavoriteGameDao {
    
    /**
     * Alle Favoriten als Flow zurückgeben (für reaktive UI-Updates).
     */
    @Query("SELECT * FROM favorite_games ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>
    
    /**
     * Prüfen ob ein Spiel als Favorit gespeichert ist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_games WHERE id = :gameId)")
    suspend fun isFavorite(gameId: Int): Boolean
    
    /**
     * Ein spezifisches Favoriten-Spiel abrufen.
     */
    @Query("SELECT * FROM favorite_games WHERE id = :gameId")
    suspend fun getFavoriteById(gameId: Int): FavoriteGameEntity?
    
    /**
     * Favorit hinzufügen oder aktualisieren.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteGameEntity)
    
    /**
     * Favorit entfernen.
     */
    @Query("DELETE FROM favorite_games WHERE id = :gameId")
    suspend fun removeFavorite(gameId: Int)
    
    /**
     * Alle Favoriten löschen.
     */
    @Query("DELETE FROM favorite_games")
    suspend fun clearAllFavorites()
    
    /**
     * Anzahl der Favoriten.
     */
    @Query("SELECT COUNT(*) FROM favorite_games")
    suspend fun getFavoriteCount(): Int
    
    /**
     * Favoriten nach Titel suchen.
     */
    @Query("SELECT * FROM favorite_games WHERE title LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteGameEntity>>
} 