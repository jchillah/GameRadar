package de.syntax_institut.androidabschlussprojekt.data.repositories

import de.syntax_institut.androidabschlussprojekt.data.local.dao.FavoriteGameDao
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toFavoriteEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository für Favoriten-Operationen.
 */
class FavoritesRepository @Inject constructor(
    private val favoriteGameDao: FavoriteGameDao
) {
    
    /**
     * Alle Favoriten als Flow zurückgeben.
     */
    fun getAllFavorites(): Flow<List<Game>> {
        return favoriteGameDao.getAllFavorites().map { entities ->
            entities.map { it.toGame() }
        }
    }
    
    /**
     * Prüfen ob ein Spiel als Favorit gespeichert ist.
     */
    suspend fun isFavorite(gameId: Int): Boolean {
        return favoriteGameDao.isFavorite(gameId)
    }
    
    /**
     * Ein spezifisches Favoriten-Spiel abrufen.
     */
    suspend fun getFavoriteById(gameId: Int): Game? {
        return favoriteGameDao.getFavoriteById(gameId)?.toGame()
    }
    
    /**
     * Spiel zu Favoriten hinzufügen.
     */
    suspend fun addFavorite(game: Game): Resource<Unit> {
        return try {
            favoriteGameDao.insertFavorite(game.toFavoriteEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Fehler beim Hinzufügen des Favoriten: ${e.localizedMessage}")
        }
    }
    
    /**
     * Spiel aus Favoriten entfernen.
     */
    suspend fun removeFavorite(gameId: Int): Resource<Unit> {
        return try {
            favoriteGameDao.removeFavorite(gameId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Fehler beim Entfernen des Favoriten: ${e.localizedMessage}")
        }
    }
    
    /**
     * Favorit umschalten (hinzufügen wenn nicht vorhanden, entfernen wenn vorhanden).
     */
    suspend fun toggleFavorite(game: Game): Resource<Boolean> {
        return try {
            val isCurrentlyFavorite = favoriteGameDao.isFavorite(game.id)
            if (isCurrentlyFavorite) {
                favoriteGameDao.removeFavorite(game.id)
                Resource.Success(false) // Nicht mehr favorisiert
            } else {
                favoriteGameDao.insertFavorite(game.toFavoriteEntity())
                Resource.Success(true) // Jetzt favorisiert
            }
        } catch (e: Exception) {
            Resource.Error("Fehler beim Umschalten des Favoriten: ${e.localizedMessage}")
        }
    }
    
    /**
     * Alle Favoriten löschen.
     */
    suspend fun clearAllFavorites(): Resource<Unit> {
        return try {
            favoriteGameDao.clearAllFavorites()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Fehler beim Löschen aller Favoriten: ${e.localizedMessage}")
        }
    }
    
    /**
     * Anzahl der Favoriten.
     */
    suspend fun getFavoriteCount(): Int {
        return favoriteGameDao.getFavoriteCount()
    }
    
    /**
     * Favoriten nach Titel suchen.
     */
    fun searchFavorites(query: String): Flow<List<Game>> {
        return favoriteGameDao.searchFavorites(query).map { entities ->
            entities.map { it.toGame() }
        }
    }
} 