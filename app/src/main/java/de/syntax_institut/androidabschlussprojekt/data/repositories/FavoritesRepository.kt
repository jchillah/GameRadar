package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.util.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
    )
    private val movieListAdapter = moshi.adapter<List<Movie>>(
        Types.newParameterizedType(
            List::class.java,
            Movie::class.java
        )
    )
    
    /**
     * Alle Favoriten als Flow zurückgeben.
     */
    fun getAllFavorites(): Flow<List<Game>> {
        return favoriteGameDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                try {
                    Log.d("FavoritesRepository", "[DEBUG] Konvertiere Entity: ${entity.title}")
                    Log.d(
                        "FavoritesRepository",
                        "[DEBUG] Entity Screenshots JSON: ${entity.screenshots}"
                    )
                    Log.d("FavoritesRepository", "[DEBUG] Entity Movies JSON: ${entity.movies}")

                    val game = entity.toGame()
                    Log.d("FavoritesRepository", "[DEBUG] Konvertiert zu Game: ${game.title}")
                    Log.d(
                        "FavoritesRepository",
                        "[DEBUG] Game Screenshots: ${game.screenshots.size}"
                    )
                    Log.d("FavoritesRepository", "[DEBUG] Game Movies: ${game.movies.size}")
                    game
                } catch (e: Exception) {
                    Log.e(
                        "FavoritesRepository",
                        "Fehler beim Konvertieren von Entity: ${e.message}"
                    )
                    Log.e(
                        "FavoritesRepository",
                        "Verwende Fallback für Entity: ${entity.title}"
                    )
                    // Fallback: Versuche das Spiel neu zu laden, nur wenn die Konvertierung fehlschlägt
                    // WICHTIG: Verwende die gespeicherten Screenshots und Movies, wenn sie vorhanden sind
                    val fallbackGame = repo.getGameDetail(entity.id).data
                    if (fallbackGame != null) {
                        Log.d(
                            "FavoritesRepository",
                            "[DEBUG] Fallback erfolgreich für: ${entity.title}"
                        )
                        // Behalte die ursprünglichen Screenshots und Movies, wenn sie vorhanden sind
                        val originalScreenshots = try {
                            val result =
                                stringListAdapter.fromJson(entity.screenshots) ?: emptyList()
                            if (result.isNotEmpty()) result else fallbackGame.screenshots
                        } catch (_: Exception) {
                            fallbackGame.screenshots
                        }

                        val originalMovies = try {
                            val result = movieListAdapter.fromJson(entity.movies) ?: emptyList()
                            if (result.isNotEmpty()) result else fallbackGame.movies
                        } catch (_: Exception) {
                            fallbackGame.movies
                        }

                        fallbackGame.copy(
                            screenshots = originalScreenshots,
                            movies = originalMovies
                        )
                    } else {
                        Log.w(
                            "FavoritesRepository",
                            "[WARN] Fallback fehlgeschlagen für: ${entity.title}"
                        )
                        // Erstelle ein minimales Game-Objekt als letzter Fallback
                        Game(
                            id = entity.id,
                            slug = entity.slug,
                            title = entity.title,
                            releaseDate = entity.releaseDate,
                            imageUrl = entity.imageUrl,
                            rating = entity.rating,
                            description = entity.description,
                            metacritic = entity.metacritic,
                            website = entity.website,
                            esrbRating = entity.esrbRating
                        )
                    }
                }
            }
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
        return try {
            favoriteGameDao.getFavoriteById(gameId)?.toGame()
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Fehler beim Laden des Favoriten: ${e.message}")
            null
        }
    }
    
    /**
     * Spiel zu Favoriten hinzufügen.
     */
    suspend fun addFavorite(game: Game): Resource<Unit> {
        return try {
            Log.d("FavoritesRepository", "[DEBUG] Füge Favorit hinzu: ${game.title}")
            Log.d(
                "FavoritesRepository",
                "[DEBUG] Ursprüngliche Screenshots: ${game.screenshots.size}, Movies: ${game.movies.size}"
            )

            // IMMER vollständige Details laden, um sicherzustellen, dass wir die besten verfügbaren Daten haben
            Log.d("FavoritesRepository", "[DEBUG] Lade vollständige Details für: ${game.title}")
                val detailResult = repo.getGameDetail(game.id)
            val fullGame = when (detailResult) {
                is Resource.Success -> {
                    val detailedGame = detailResult.data ?: game
                    Log.d(
                        "FavoritesRepository",
                        "[DEBUG] Details geladen - Screenshots: ${detailedGame.screenshots.size}, Movies: ${detailedGame.movies.size}"
                    )
                    // WICHTIG: Behalte ursprüngliche Screenshots und Movies, wenn sie vorhanden sind
                    val mergedGame = detailedGame.copy(
                        screenshots = if (detailedGame.screenshots.isNotEmpty()) detailedGame.screenshots else game.screenshots,
                        movies = if (detailedGame.movies.isNotEmpty()) detailedGame.movies else game.movies
                    )
                    Log.d(
                        "FavoritesRepository",
                        "[DEBUG] Nach Merge - Screenshots: ${mergedGame.screenshots.size}, Movies: ${mergedGame.movies.size}"
                    )
                    mergedGame
                }

                else -> {
                    Log.w(
                        "FavoritesRepository",
                        "[WARN] Konnte Details nicht laden, verwende ursprüngliches Spiel"
                    )
                    game
                }
            }

            val entity = fullGame.toFavoriteEntity()
            favoriteGameDao.insertFavorite(entity)
            Log.d("FavoritesRepository", "[DEBUG] Favorit erfolgreich gespeichert")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Fehler beim Hinzufügen des Favoriten: ${e.message}")
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
                Log.d("FavoritesRepository", "[DEBUG] Umschalte Favorit: ${game.title}")
                Log.d(
                    "FavoritesRepository",
                    "[DEBUG] Ursprüngliche Screenshots: ${game.screenshots.size}, Movies: ${game.movies.size}"
                )

                // IMMER vollständige Details laden, um sicherzustellen, dass wir die besten verfügbaren Daten haben
                Log.d(
                    "FavoritesRepository",
                    "[DEBUG] Lade vollständige Details für Toggle: ${game.title}"
                )
                val detailResult = repo.getGameDetail(game.id)
                val fullGame = when (detailResult) {
                    is Resource.Success -> {
                        val detailedGame = detailResult.data ?: game
                        Log.d(
                            "FavoritesRepository",
                            "[DEBUG] Toggle Details geladen - Screenshots: ${detailedGame.screenshots.size}, Movies: ${detailedGame.movies.size}"
                        )
                        // WICHTIG: Behalte ursprüngliche Screenshots und Movies, wenn sie vorhanden sind
                        val mergedGame = detailedGame.copy(
                            screenshots = if (detailedGame.screenshots.isNotEmpty()) detailedGame.screenshots else game.screenshots,
                            movies = if (detailedGame.movies.isNotEmpty()) detailedGame.movies else game.movies
                        )
                        Log.d(
                            "FavoritesRepository",
                            "[DEBUG] Toggle nach Merge - Screenshots: ${mergedGame.screenshots.size}, Movies: ${mergedGame.movies.size}"
                        )
                        mergedGame
                    }

                    else -> {
                        Log.w(
                            "FavoritesRepository",
                            "[WARN] Konnte Toggle-Details nicht laden, verwende ursprüngliches Spiel"
                        )
                        game
                    }
                }

                val entity = fullGame.toFavoriteEntity()
                favoriteGameDao.insertFavorite(entity)
                Log.d("FavoritesRepository", "[DEBUG] Toggle erfolgreich gespeichert")
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Fehler beim Umschalten des Favoriten: ${e.message}")
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
            Resource.Error("Fehler beim Löschen der Favoriten: ${e.localizedMessage}")
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
            entities.map { entity ->
                try {
                    entity.toGame()
                } catch (e: Exception) {
                    Log.e(
                        "FavoritesRepository",
                        "Fehler beim Konvertieren von Entity in Suche: ${e.message}"
                    )
                    Game(
                        id = entity.id,
                        slug = entity.slug,
                        title = entity.title,
                        releaseDate = entity.releaseDate,
                        imageUrl = entity.imageUrl,
                        rating = entity.rating,
                        description = entity.description,
                        metacritic = entity.metacritic,
                        website = entity.website,
                        esrbRating = entity.esrbRating
                    )
                }
            }
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