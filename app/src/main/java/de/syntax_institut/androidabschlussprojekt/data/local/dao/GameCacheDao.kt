package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Game-Cache Operationen.
 * Bietet Methoden zum Cachen, Abrufen, Löschen und Prüfen von Spielen im lokalen Cache.
 */
@Dao
interface GameCacheDao {
    /**
     * Speichert oder aktualisiert ein Spiel im Cache.
     * @param game Das zu speichernde GameCacheEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameCacheEntity)

    /**
     * Speichert mehrere Spiele im Cache.
     * @param games Die zu speichernden GameCacheEntity-Objekte
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameCacheEntity>)

    /**
     * Gibt ein Spiel aus dem Cache anhand der ID zurück.
     * @param gameId Die ID des gesuchten Spiels
     * @return Das gefundene GameCacheEntity oder null
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): GameCacheEntity?

    /**
     * Gibt alle Spiele aus dem Cache als Flow zurück.
     * @return Flow mit einer Liste aller GameCacheEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " ORDER BY cachedAt DESC")
    fun getAllCachedGames(): Flow<List<GameCacheEntity>>

    /**
     * Gibt Spiele aus dem Cache anhand eines Suchquerys zurück.
     * @param query Der Suchbegriff
     * @return Flow mit einer Liste der gefundenen GameCacheEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query ORDER BY cachedAt DESC")
    fun getGamesByQuery(query: String): Flow<List<GameCacheEntity>>

    /**
     * Gibt Spiele aus dem Cache anhand eines Suchquerys und Filter-Hash zurück.
     * @param query Der Suchbegriff
     * @param filterHash Der Filter-Hash
     * @return Flow mit einer Liste der gefundenen GameCacheEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query AND filterHash = :filterHash ORDER BY cachedAt DESC")
    fun getGamesByQueryAndFilter(query: String, filterHash: String): Flow<List<GameCacheEntity>>

    /**
     * Entfernt ein Spiel anhand der ID aus dem Cache.
     * @param gameId Die ID des zu entfernenden Spiels
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun removeGame(gameId: Int)

    /**
     * Entfernt alle Spiele aus dem Cache.
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun clearAllGames()

    /**
     * Entfernt alte Cache-Einträge, die älter als maxAge sind.
     * @param maxAge Zeitstempel, älter als dieser Wert wird gelöscht
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE cachedAt < :maxAge")
    suspend fun clearOldCache(maxAge: Long)

    /**
     * Gibt die Anzahl der gecachten Spiele zurück.
     * @return Die Anzahl der gespeicherten Spiele
     */
    @Query("SELECT COUNT(*) FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun getCacheSize(): Int

    /**
     * Prüft, ob ein Spiel im Cache vorhanden ist.
     * @param gameId Die ID des zu prüfenden Spiels
     * @return true, wenn das Spiel im Cache ist, sonst false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_CACHE_TABLE + " WHERE id = :gameId)")
    suspend fun isGameCached(gameId: Int): Boolean

    /**
     * Prüft, ob ein Suchquery im Cache vorhanden ist.
     * @param query Der Suchbegriff
     * @return true, wenn der Query im Cache ist, sonst false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_CACHE_TABLE + " WHERE searchQuery = :query)")
    suspend fun isQueryCached(query: String): Boolean

    /**
     * Gibt den ältesten Cache-Zeitstempel zurück.
     * @return Der älteste Zeitstempel oder null
     */
    @Query("SELECT MIN(cachedAt) FROM " + Constants.GAME_CACHE_TABLE)
    suspend fun getOldestCacheTime(): Long?

    /**
     * Entfernt abgelaufene Spiele aus dem Cache.
     * @param timestamp Zeitstempel, älter als dieser Wert wird gelöscht
     */
    @Query("DELETE FROM " + Constants.GAME_CACHE_TABLE + " WHERE cachedAt < :timestamp")
    suspend fun deleteExpiredGames(timestamp: Long)
} 