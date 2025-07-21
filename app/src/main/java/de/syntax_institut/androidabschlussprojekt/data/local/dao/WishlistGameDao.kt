package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*

/**
 * DAO für Operationen auf der Wunschliste.
 * Bietet Methoden zum Abrufen, Einfügen, Löschen und Suchen von Spielen in der Wunschliste.
 */
@Dao
interface WishlistGameDao {
    /**
     * Gibt alle Spiele in der Wunschliste als Flow zurück.
     * @return Flow mit einer Liste aller WishlistGameEntity-Objekte
     */
    @Query("SELECT * FROM wishlist_games")
    fun getAllWishlistGames(): Flow<List<WishlistGameEntity>>

    /**
     * Gibt ein Spiel aus der Wunschliste anhand der ID zurück.
     * @param gameId Die ID des gesuchten Spiels
     * @return Das gefundene WishlistGameEntity oder null
     */
    @Query("SELECT * FROM wishlist_games WHERE id = :gameId LIMIT 1")
    suspend fun getWishlistGameById(gameId: Int): WishlistGameEntity?

    /**
     * Fügt ein Spiel zur Wunschliste hinzu oder aktualisiert es.
     * @param game Das hinzuzufügende WishlistGameEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistGame(game: WishlistGameEntity)

    /**
     * Entfernt ein Spiel anhand der ID aus der Wunschliste.
     * @param gameId Die ID des zu entfernenden Spiels
     */
    @Query("DELETE FROM wishlist_games WHERE id = :gameId")
    suspend fun removeWishlistGame(gameId: Int)

    /**
     * Prüft, ob ein Spiel in der Wunschliste ist.
     * @param gameId Die ID des zu prüfenden Spiels
     * @return true, wenn das Spiel in der Wunschliste ist, sonst false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_games WHERE id = :gameId)")
    suspend fun isInWishlist(gameId: Int): Boolean

    /**
     * Löscht alle Spiele aus der Wunschliste.
     */
    @Query("DELETE FROM wishlist_games")
    suspend fun clearAllWishlistGames()

    /**
     * Sucht Spiele in der Wunschliste anhand eines Suchbegriffs.
     * @param query Der Suchbegriff für den Spieletitel
     * @return Flow mit einer Liste der gefundenen WishlistGameEntity-Objekte
     */
    @Query(
        "SELECT * FROM wishlist_games WHERE title LIKE '%' || :query || '%' COLLATE NOCASE ORDER BY title ASC"
    )
    fun searchWishlistGames(query: String): Flow<List<WishlistGameEntity>>
}
