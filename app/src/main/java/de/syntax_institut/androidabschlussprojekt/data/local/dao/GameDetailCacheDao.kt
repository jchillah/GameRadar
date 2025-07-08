package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Detail-Cache Operationen (Detailansicht).
 */
@Dao
interface GameDetailCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetail(game: GameDetailCacheEntity)

    @Query("SELECT * FROM game_detail_cache WHERE id = :gameId")
    suspend fun getGameDetailById(gameId: Int): GameDetailCacheEntity?

    @Query("DELETE FROM game_detail_cache WHERE id = :gameId")
    suspend fun removeGameDetail(gameId: Int)

    @Query("DELETE FROM game_detail_cache")
    suspend fun clearAllGameDetails()

    @Query("SELECT COUNT(*) FROM game_detail_cache")
    suspend fun getDetailCacheSize(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM game_detail_cache WHERE id = :gameId)")
    suspend fun isGameDetailCached(gameId: Int): Boolean

    @Query("SELECT * FROM game_detail_cache ORDER BY detailCachedAt DESC")
    fun getAllGameDetails(): Flow<List<GameDetailCacheEntity>>
} 