package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Detail-Cache Operationen (Detailansicht).
 * Bietet Methoden zum Cachen, Abrufen, Löschen und Prüfen von Detaildaten im lokalen Cache.
 */
@Dao
interface GameDetailCacheDao {
    /**
     * Speichert oder aktualisiert Spieldetails im Cache.
     * @param game Das zu speichernde GameDetailCacheEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetail(game: GameDetailCacheEntity)

    /**
     * Gibt Spieldetails anhand der ID zurück.
     * @param gameId Die ID des gesuchten Spiels
     * @return Das gefundene GameDetailCacheEntity oder null
     */
    @Query("SELECT * FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun getGameDetailById(gameId: Int): GameDetailCacheEntity?

    /**
     * Entfernt Spieldetails anhand der ID aus dem Cache.
     * @param gameId Die ID des zu entfernenden Spiels
     */
    @Query("DELETE FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun removeGameDetail(gameId: Int)

    /**
     * Entfernt alle Spieldetails aus dem Cache.
     */
    @Query("DELETE FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun clearAllGameDetails()

    /**
     * Gibt die Anzahl der gecachten Spieldetails zurück.
     * @return Die Anzahl der gespeicherten Spieldetails
     */
    @Query("SELECT COUNT(*) FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun getDetailCacheSize(): Int

    /**
     * Prüft, ob Spieldetails im Cache vorhanden sind.
     * @param gameId Die ID des zu prüfenden Spiels
     * @return true, wenn Details im Cache sind, sonst false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId)")
    suspend fun isGameDetailCached(gameId: Int): Boolean

    /**
     * Gibt alle Spieldetails aus dem Cache als Flow zurück.
     * @return Flow mit einer Liste aller GameDetailCacheEntity-Objekte
     */
    @Query("SELECT * FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " ORDER BY detailCachedAt DESC")
    fun getAllGameDetails(): Flow<List<GameDetailCacheEntity>>

    /**
     * Gibt den ältesten Detail-Cache-Zeitstempel zurück.
     * @return Der älteste Zeitstempel oder null
     */
    @Query("SELECT MIN(detailCachedAt) FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun getOldestDetailCacheTime(): Long?
} 