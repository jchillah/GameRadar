package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Game-Cache Operationen.
 */
@Dao
interface GameCacheDao {

    /**
     * Spiel in Cache speichern oder aktualisieren.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameCacheEntity)

    /**
     * Mehrere Spiele in Cache speichern.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameCacheEntity>)
    
    /**
     * Spiel aus Cache abrufen.
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): GameCacheEntity?
    
    /**
     * Alle Spiele aus Cache abrufen.
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " ORDER BY cachedAt DESC")
    fun getAllCachedGames(): Flow<List<GameCacheEntity>>
    
    /**
     * Spiele nach Suchquery abrufen.
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query ORDER BY cachedAt DESC")
    fun getGamesByQuery(query: String): Flow<List<GameCacheEntity>>
    
    /**
     * Spiele nach Suchquery und Filter abrufen.
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query AND filterHash = :filterHash ORDER BY cachedAt DESC")
    fun getGamesByQueryAndFilter(query: String, filterHash: String): Flow<List<GameCacheEntity>>
    
    /**
     * Spiel aus Cache entfernen.
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun removeGame(gameId: Int)
    
    /**
     * Alle Spiele aus Cache entfernen.
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun clearAllGames()
    
    /**
     * Alte Cache-Einträge entfernen (älter als maxAge).
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE cachedAt < :maxAge")
    suspend fun clearOldCache(maxAge: Long)
    
    /**
     * Anzahl der gecachten Spiele.
     */
    @Query("SELECT COUNT(*) FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun getCacheSize(): Int
    
    /**
     * Prüfen ob Spiel im Cache vorhanden ist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId)")
    suspend fun isGameCached(gameId: Int): Boolean
    
    /**
     * Prüfen ob Suchquery im Cache vorhanden ist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query)")
    suspend fun isQueryCached(query: String): Boolean
    
    /**
     * Ältesten Cache-Zeitstempel abrufen.
     */
    @Query("SELECT MIN(cachedAt) FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun getOldestCacheTime(): Long?

    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE cachedAt < :timestamp")
    suspend fun deleteExpiredGames(timestamp: Long)
} 