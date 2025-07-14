package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

@Dao
interface WishlistGameDao {
    @Query("SELECT * FROM wishlist_games")
    fun getAllWishlistGames(): Flow<List<WishlistGameEntity>>

    @Query("SELECT * FROM wishlist_games WHERE id = :gameId LIMIT 1")
    suspend fun getWishlistGameById(gameId: Int): WishlistGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistGame(game: WishlistGameEntity)

    @Query("DELETE FROM wishlist_games WHERE id = :gameId")
    suspend fun removeWishlistGame(gameId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_games WHERE id = :gameId)")
    suspend fun isInWishlist(gameId: Int): Boolean

    @Query("DELETE FROM wishlist_games")
    suspend fun clearAllWishlistGames()

    @Query("SELECT * FROM wishlist_games WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchWishlistGames(query: String): Flow<List<WishlistGameEntity>>
}
