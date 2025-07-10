package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Detail-Cache Operationen (Detailansicht).
 */
@Dao
interface GameDetailCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetail(game: GameDetailCacheEntity)

    @Query("SELECT * FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun getGameDetailById(gameId: Int): GameDetailCacheEntity?

    @Query("DELETE FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId")
    suspend fun removeGameDetail(gameId: Int)

    @Query("DELETE FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun clearAllGameDetails()

    @Query("SELECT COUNT(*) FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun getDetailCacheSize(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " WHERE id = :gameId)")
    suspend fun isGameDetailCached(gameId: Int): Boolean

    @Query("SELECT * FROM " + Constants.GAME_DETAIL_CACHE_TABLE + " ORDER BY detailCachedAt DESC")
    fun getAllGameDetails(): Flow<List<GameDetailCacheEntity>>

    @Query("SELECT MIN(detailCachedAt) FROM " + Constants.GAME_DETAIL_CACHE_TABLE)
    suspend fun getOldestDetailCacheTime(): Long?
} 