package de.syntax_institut.androidabschlussprojekt.data.repositories

import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toFavoriteEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Repository für Favoriten-Operationen.
 */
class FavoritesRepository @Inject constructor(
    private val favoriteGameDao: FavoriteGameDao,
    private val repo: GameRepository,
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
            val fullGame = if (game.screenshots.isEmpty() || game.website.isNullOrBlank()) {
                // Hole vollständige Details nach
                val detailResult = repo.getGameDetail(game.id)
                when (detailResult) {
                    is Resource.Success -> detailResult.data ?: game
                    else -> game
                }
            } else game
            favoriteGameDao.insertFavorite(fullGame.toFavoriteEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Fehler beim Hinzufügen des Favoriten: " + e.localizedMessage)
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
                Resource.Success(false)
            } else {
                val fullGame = if (game.screenshots.isEmpty() || game.website.isNullOrBlank()) {
                    val detailResult = repo.getGameDetail(game.id)
                    when (detailResult) {
                        is Resource.Success -> detailResult.data ?: game
                        else -> game
                    }
                } else game
                favoriteGameDao.insertFavorite(fullGame.toFavoriteEntity())
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error("Fehler beim Umschalten des Favoriten: " + e.localizedMessage)
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

    /**
     * Synchronisiert alle Favoriten mit den aktuellen Daten aus der API.
     * Aktualisiert lokale Einträge, wenn sich etwas geändert hat.
     */
    suspend fun syncFavoritesWithApi(rawgApi: de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi) {
        val localFavorites =
            favoriteGameDao.getAllFavorites().firstOrNull()?.map { it.toGame() } ?: return
        for (fav in localFavorites) {
            try {
                val response = rawgApi.getGameDetail(fav.id)
                if (response.isSuccessful) {
                    val apiGameDto = response.body()
                    if (apiGameDto != null) {
                        val apiGame = apiGameDto.toDomain()
                        if (apiGame != fav) {
                            favoriteGameDao.insertFavorite(apiGame.toFavoriteEntity())
                        }
                    }
                }
            } catch (_: Exception) { /* Fehler ignorieren, nächster Favorit */
            }
        }
    }
} 