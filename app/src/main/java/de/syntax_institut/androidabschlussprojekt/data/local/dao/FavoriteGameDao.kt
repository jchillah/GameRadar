package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Favoriten-Spiele Operationen.
 * Bietet Methoden zum Abrufen, Hinzufügen, Löschen und Suchen von Favoriten-Spielen.
 */
@Dao
interface FavoriteGameDao {
    /**
     * Gibt alle Favoriten als Flow zurück (für reaktive UI-Updates).
     * @return Flow mit einer Liste aller FavoriteGameEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>

    /**
     * Prüft, ob ein Spiel als Favorit gespeichert ist.
     * @param gameId Die ID des zu prüfenden Spiels
     * @return true, wenn das Spiel Favorit ist, sonst false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId)")
    suspend fun isFavorite(gameId: Int): Boolean

    /**
     * Gibt ein spezifisches Favoriten-Spiel anhand der ID zurück.
     * @param gameId Die ID des gesuchten Spiels
     * @return Das gefundene FavoriteGameEntity oder null
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId")
    suspend fun getFavoriteById(gameId: Int): FavoriteGameEntity?

    /**
     * Fügt ein Spiel zu den Favoriten hinzu oder aktualisiert es.
     * @param favorite Das hinzuzufügende FavoriteGameEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteGameEntity)

    /**
     * Entfernt ein Spiel anhand der ID aus den Favoriten.
     * @param gameId Die ID des zu entfernenden Spiels
     */
    @Query("DELETE FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE id = :gameId")
    suspend fun removeFavorite(gameId: Int)

    /**
     * Löscht alle Favoriten.
     */
    @Query("DELETE FROM " + Constants.FAVORITE_GAME_TABLE)
    suspend fun clearAllFavorites()

    /**
     * Gibt die Anzahl der Favoriten zurück.
     * @return Die Anzahl der gespeicherten Favoriten
     */
    @Query("SELECT COUNT(*) FROM " + Constants.FAVORITE_GAME_TABLE)
    suspend fun getFavoriteCount(): Int

    /**
     * Sucht Favoriten anhand eines Suchbegriffs im Titel.
     * @param query Der Suchbegriff für den Spieletitel
     * @return Flow mit einer Liste der gefundenen FavoriteGameEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.FAVORITE_GAME_TABLE + " WHERE title LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteGameEntity>>
} 