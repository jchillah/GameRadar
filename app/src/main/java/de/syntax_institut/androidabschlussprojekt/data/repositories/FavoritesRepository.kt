package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import android.net.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toFavoriteEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import java.io.*
import javax.inject.*

/**
 * Repository for managing favorite games.
 * Handles all database operations and import/export functionality for favorites.
 *
 * @property favoriteGameDao DAO for favorite games table
 * @property repo GameRepository for game details
 */
@Singleton
class FavoritesRepository @Inject constructor(
    private val favoriteGameDao: FavoriteGameDao,
    private val repo: GameRepository,
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    private val movieListAdapter = moshi.adapter<List<Movie>>(
        Types.newParameterizedType(List::class.java, Movie::class.java)
    )

    /**
     * Get all favorite games as a Flow.
     * @return Flow emitting a list of favorite games
     */
    fun getAllFavorites(): Flow<List<
            Game>> {
        return favoriteGameDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                try {
                    AppLogger.d(
                        "FavoritesRepository",
                        "Converting entity: ${entity.title}"
                    )
                    AppLogger.d(
                        "FavoritesRepository",
                        "Entity screenshots JSON: ${entity.screenshots}"
                    )
                    AppLogger.d(
                        "FavoritesRepository",
                        "Entity movies JSON: ${entity.movies}"
                    )

                    val game = entity.toGame()
                    AppLogger.d(
                        "FavoritesRepository",
                        "Converted to Game: ${game.title}"
                    )
                    AppLogger.d(
                        "FavoritesRepository",
                        "Game screenshots: ${game.screenshots.size}"
                    )
                    AppLogger.d(
                        "FavoritesRepository",
                        "Game movies: ${game.movies.size}"
                    )
                    game
                } catch (e: Exception) {
                    handleEntityConversionError(e, entity)
                }
            }
        }
    }

    private fun handleEntityConversionError(e: Exception, entity: Any): Game {
        val errorMsg = "${Constants.ERROR} converting entity: ${e.message}"
        CrashlyticsHelper.recordException(e)
        AppLogger.e("FavoritesRepository", errorMsg)

        val entityTitle = when (entity) {
            is Game -> entity.title
            is FavoriteGameEntity -> entity.title
            else -> "Unknown"
        }
        AppLogger.e("FavoritesRepository", "Using fallback for entity: $entityTitle")

        // Create a minimal game object as fallback
        return Game(
            id = when (entity) {
                is Game -> entity.id
                is FavoriteGameEntity -> entity.id
                else -> 0
            },
            slug = when (entity) {
                is Game -> entity.slug
                is FavoriteGameEntity -> entity.slug
                else -> ""
            },
            title = "Error loading game: $entityTitle",
            releaseDate = when (entity) {
                is Game -> entity.releaseDate
                is FavoriteGameEntity -> entity.releaseDate
                else -> ""
            },
            imageUrl = when (entity) {
                is Game -> entity.imageUrl
                is FavoriteGameEntity -> entity.imageUrl
                else -> null
            },
            rating = when (entity) {
                is Game -> entity.rating
                is FavoriteGameEntity -> entity.rating
                else -> 0f
            },
            description = "Error loading game details",
            metacritic = when (entity) {
                is Game -> entity.metacritic
                is FavoriteGameEntity -> entity.metacritic
                else -> null
            },
            website = when (entity) {
                is Game -> entity.website
                is FavoriteGameEntity -> entity.website
                else -> ""
            },
            esrbRating = when (entity) {
                is Game -> entity.esrbRating
                is FavoriteGameEntity -> entity.esrbRating
                else -> null
            },
            screenshots = emptyList(),
            movies = emptyList()
        )
    }

    private fun mergeWithOriginalMedia(entity: Any, fallbackGame: Game): Game {
        val originalScreenshots = when (entity) {
            is Game -> entity.screenshots.ifEmpty { fallbackGame.screenshots }
            is FavoriteGameEntity -> {
                try {
                    stringListAdapter.fromJson(entity.screenshots) ?: fallbackGame.screenshots
                } catch (_: Exception) {
                    fallbackGame.screenshots
                }
            }

            else -> fallbackGame.screenshots
        }

        val originalMovies = when (entity) {
            is Game -> entity.movies.ifEmpty { fallbackGame.movies }
            is FavoriteGameEntity -> {
                try {
                    movieListAdapter.fromJson(entity.movies) ?: fallbackGame.movies
                } catch (_: Exception) {
                    fallbackGame.movies
                }
            }

            else -> fallbackGame.movies
        }

        return when (entity) {
            is Game -> entity.copy(
                screenshots = originalScreenshots,
                movies = originalMovies
            )

            is FavoriteGameEntity -> entity.toGame().copy(
                screenshots = originalScreenshots,
                movies = originalMovies
            )

            else -> fallbackGame
        }
    }

    /** Prüfen ob ein Spiel als Favorit gespeichert ist. */
    suspend fun isFavorite(gameId: Int): Boolean {
        return favoriteGameDao.isFavorite(gameId)
    }

    /** Ein spezifisches Favoriten-Spiel abrufen. */
    suspend fun getFavoriteById(gameId: Int): Game? {
        return try {
            favoriteGameDao.getFavoriteById(gameId)?.let { entity ->
                try {
                    entity.toGame()
                } catch (e: Exception) {
                    handleEntityConversionError(e, entity)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "${Constants.ERROR} loading favorite: ${e.message}"
            CrashlyticsHelper.recordException(e)
            AppLogger.e("FavoritesRepository", errorMsg)
            null
        }
    }

    /**
     * Fügt ein Spiel zu den Favoriten hinzu.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param game Das hinzuzufügende Spiel
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun addFavorite(context: Context, game: Game): Resource<Unit> {
        PerformanceMonitor.startTimer("db_addFavorite")
        return try {
            AppLogger.d("FavoritesRepository", "Füge Favorit hinzu: ${game.title}")
            AppLogger.d(
                "FavoritesRepository",
                "Ursprüngliche Screenshots: ${game.screenshots.size}, Movies: ${game.movies.size}"
            )

            // IMMER vollständige Details laden, um sicherzustellen, dass wir die besten
            // verfügbaren
            // Daten haben
            AppLogger.d(
                "FavoritesRepository",
                "Lade vollständige Details für: ${game.title}"
            )
            val detailResult = repo.getGameDetail(game.id)
            val fullGame =
                when (detailResult) {
                    is Resource.Success -> {
                        val detailedGame = detailResult.data ?: game
                        AppLogger.d(
                            "FavoritesRepository",
                            "Details geladen - Screenshots: ${detailedGame.screenshots.size}, Movies: ${detailedGame.movies.size}"
                        )
                        detailedGame
                    }

                    else -> {
                        AppLogger.w(
                            "FavoritesRepository",
                            "Konnte Details nicht laden, verwende ursprüngliches Spiel"
                        )
                        game
                    }
                }

            // Merge-Logik: Vorhandene Favoriten berücksichtigen
            val existingEntity = favoriteGameDao.getFavoriteById(game.id)
            val existingGame = existingEntity?.toGame()
            val mergedGame =
                fullGame.copy(
                    screenshots =
                        when {
                            fullGame.screenshots.isNotEmpty() ->
                                fullGame.screenshots

                            game.screenshots.isNotEmpty() ->
                                game.screenshots

                            existingGame?.screenshots?.isNotEmpty() ==
                                    true -> {
                                AppLogger.d(
                                    "FavoritesRepository",
                                    "Übernehme alte Screenshots aus DB (${existingGame.screenshots.size}) für ${game.title}"
                                )
                                existingGame.screenshots
                            }

                            else -> emptyList()
                        },
                    movies =
                        when {
                            fullGame.movies.isNotEmpty() ->
                                fullGame.movies

                            game.movies.isNotEmpty() -> game.movies
                            existingGame?.movies?.isNotEmpty() ==
                                    true -> {
                                AppLogger.d(
                                    "FavoritesRepository",
                                    "Übernehme alte Movies aus DB (${existingGame.movies.size}) für ${game.title}"
                                )
                                existingGame.movies
                            }

                            else -> emptyList()
                        }
                )
            AppLogger.d(
                "FavoritesRepository",
                "Nach Merge - Screenshots: ${mergedGame.screenshots.size}, Movies: ${mergedGame.movies.size}"
            )

            val entity = mergedGame.toFavoriteEntity()
            favoriteGameDao.insertFavorite(entity)
            AppLogger.d("FavoritesRepository", "Favorit erfolgreich gespeichert")
            Resource.Success(Unit)
        } catch (e: Exception) {
            CrashlyticsHelper.recordDatabaseError(
                "insert",
                Constants.FAVORITE_GAME_TABLE,
                e.localizedMessage ?: e.toString()
            )
            CrashlyticsHelper.recordFavoriteError(
                "addFavorite",
                gameId = game.id,
                error = e.localizedMessage ?: e.toString()
            )
            AppLogger.e(
                "FavoritesRepository",
                "${Constants.ERROR} beim Hinzufügen des Favoriten: ${e.message}"
            )
            Resource.Error(context.getString(R.string.error_add_favorite))
        } finally {
            PerformanceMonitor.endTimer("db_addFavorite")
            PerformanceMonitor.trackDatabaseOperation(
                "insert",
                Constants.FAVORITE_GAME_TABLE,
                success = Resource.Success(Unit) == Resource.Success(Unit)
            )
        }
    }

    /** Spiel aus Favoriten entfernen. */
    suspend fun removeFavorite(context: Context, gameId: Int): Resource<Unit> {
        PerformanceMonitor.startTimer("db_removeFavorite")
        return try {
            favoriteGameDao.removeFavorite(gameId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            CrashlyticsHelper.recordDatabaseError(
                "delete",
                Constants.FAVORITE_GAME_TABLE,
                e.localizedMessage ?: e.toString()
            )
            CrashlyticsHelper.recordFavoriteError(
                "removeFavorite",
                gameId = gameId,
                error = e.localizedMessage ?: e.toString()
            )
            AppLogger.e(
                "FavoritesRepository",
                "${Constants.ERROR} beim Entfernen des Favoriten: ${e.message}"
            )
            Resource.Error(context.getString(R.string.error_remove_favorite))
        } finally {
            PerformanceMonitor.endTimer("db_removeFavorite")
            PerformanceMonitor.trackDatabaseOperation(
                "delete",
                Constants.FAVORITE_GAME_TABLE,
                success = Resource.Success(Unit) == Resource.Success(Unit)
            )
        }
    }

    /** Favorit umschalten (hinzufügen wenn nicht vorhanden, entfernen wenn vorhanden). */
    suspend fun toggleFavorite(context: Context, game: Game): Resource<Boolean> {
        PerformanceMonitor.startTimer("db_toggleFavorite")
        return try {
            val isCurrentlyFavorite = favoriteGameDao.isFavorite(game.id)
            if (isCurrentlyFavorite) {
                favoriteGameDao.removeFavorite(game.id)
                Resource.Success(false)
            } else {
                AppLogger.d(
                    "FavoritesRepository",
                    "Umschalte Favorit: ${game.title}"
                )
                AppLogger.d(
                    "FavoritesRepository",
                    "Ursprüngliche Screenshots: ${game.screenshots.size}, Movies: ${game.movies.size}"
                )

                // IMMER vollständige Details laden, um sicherzustellen, dass wir
                // die besten
                // verfügbaren Daten haben
                AppLogger.d(
                    "FavoritesRepository",
                    "Lade vollständige Details für Toggle: ${game.title}"
                )
                val detailResult = repo.getGameDetail(game.id)
                val fullGame =
                    when (detailResult) {
                        is Resource.Success -> {
                            val detailedGame = detailResult.data ?: game
                            AppLogger.d(
                                "FavoritesRepository",
                                "Toggle Details geladen - Screenshots: ${detailedGame.screenshots.size}, Movies: ${detailedGame.movies.size}"
                            )
                            detailedGame
                        }

                        else -> {
                            AppLogger.w(
                                "FavoritesRepository",
                                "Konnte Toggle-Details nicht laden, verwende ursprüngliches Spiel"
                            )
                            game
                        }
                    }

                // Merge-Logik: Vorhandene Favoriten berücksichtigen
                val existingEntity = favoriteGameDao.getFavoriteById(game.id)
                val existingGame = existingEntity?.toGame()
                val mergedGame =
                    fullGame.copy(
                        screenshots =
                            when {
                                fullGame.screenshots.isNotEmpty() ->
                                    fullGame.screenshots

                                game.screenshots.isNotEmpty() ->
                                    game.screenshots

                                existingGame?.screenshots
                                    ?.isNotEmpty() == true -> {
                                    AppLogger.d(
                                        "FavoritesRepository",
                                        "Übernehme alte Screenshots aus DB (${existingGame.screenshots.size}) für ${game.title}"
                                    )
                                    existingGame.screenshots
                                }

                                else -> emptyList()
                            },
                        movies =
                            when {
                                fullGame.movies.isNotEmpty() ->
                                    fullGame.movies

                                game.movies.isNotEmpty() ->
                                    game.movies

                                existingGame?.movies
                                    ?.isNotEmpty() == true -> {
                                    AppLogger.d(
                                        "FavoritesRepository",
                                        "Übernehme alte Movies aus DB (${existingGame.movies.size}) für ${game.title}"
                                    )
                                    existingGame.movies
                                }

                                else -> emptyList()
                            }
                    )
                AppLogger.d(
                    "FavoritesRepository",
                    "Toggle nach Merge - Screenshots: ${mergedGame.screenshots.size}, Movies: ${mergedGame.movies.size}"
                )

                val entity = mergedGame.toFavoriteEntity()
                favoriteGameDao.insertFavorite(entity)
                AppLogger.d("FavoritesRepository", "Toggle erfolgreich gespeichert")
                Resource.Success(true)
            }
        } catch (e: Exception) {
            CrashlyticsHelper.recordDatabaseError(
                "toggle",
                Constants.FAVORITE_GAME_TABLE,
                e.localizedMessage ?: e.toString()
            )
            CrashlyticsHelper.recordFavoriteError(
                "toggleFavorite",
                gameId = game.id,
                error = e.localizedMessage ?: e.toString()
            )
            AppLogger.e(
                "FavoritesRepository",
                "${Constants.ERROR} beim Umschalten des Favoriten: ${e.message}"
            )
            Resource.Error(context.getString(R.string.error_toggle_favorite))
        } finally {
            PerformanceMonitor.endTimer("db_toggleFavorite")
            PerformanceMonitor.trackDatabaseOperation(
                "toggle",
                Constants.FAVORITE_GAME_TABLE,
                success = Resource.Success(true) == Resource.Success(true)
            )
        }
    }

    /** Alle Favoriten löschen. */
    suspend fun clearAllFavorites(context: Context): Resource<Unit> {
        return try {
            favoriteGameDao.clearAllFavorites()
            Resource.Success(Unit)
        } catch (e: Exception) {
            CrashlyticsHelper.recordDatabaseError(
                "deleteAll",
                Constants.FAVORITE_GAME_TABLE,
                e.localizedMessage ?: e.toString()
            )
            CrashlyticsHelper.recordFavoriteError(
                "clearAllFavorites",
                gameId = -1,
                error = e.localizedMessage ?: e.toString()
            )
            AppLogger.e(
                "FavoritesRepository",
                "${Constants.ERROR} beim Löschen aller Favoriten: ${e.message}"
            )
            Resource.Error(context.getString(R.string.error_clear_favorites))
        }
    }

    /** Anzahl der Favoriten. */
    suspend fun getFavoriteCount(): Int {
        return favoriteGameDao.getFavoriteCount()
    }

    /** Favoriten nach Titel suchen. */
    fun searchFavorites(query: String): Flow<List<Game>> {
        return favoriteGameDao.searchFavorites(query).map { entities ->
            entities.map { entity ->
                try {
                    entity.toGame()
                } catch (e: Exception) {
                    val errorMsg = "${Constants.ERROR} converting entity in search: ${e.message}"
                    CrashlyticsHelper.recordException(e)
                    AppLogger.e("FavoritesRepository", errorMsg)

                    // Create a minimal game object as fallback
                    Game(
                        id = entity.id,
                        slug = entity.slug,
                        title = entity.title,
                        releaseDate = entity.releaseDate,
                        imageUrl = entity.imageUrl,
                        rating = entity.rating,
                        description = entity.description ?: "",
                        metacritic = entity.metacritic,
                        website = entity.website ?: "",
                        esrbRating = entity.esrbRating,
                        screenshots = emptyList(),
                        movies = emptyList()
                    )
                }
            }
        }
    }

    /**
     * Synchronisiert alle Favoriten mit den aktuellen Daten aus der API. Aktualisiert lokale
     * Einträge, wenn sich etwas geändert hat.
     */
    suspend fun syncFavoritesWithApi(
        rawgApi: RawgApi,
    ) {
        val localFavorites =
            favoriteGameDao.getAllFavorites().firstOrNull()?.map { it.toGame() }
                ?: return
        for (fav in localFavorites) {
            try {
                val response = rawgApi.getGameDetail(fav.id)
                if (response.isSuccessful) {
                    val apiGameDto = response.body()
                    if (apiGameDto != null) {
                        val apiGame = apiGameDto.toDomain()
                        if (apiGame != fav) {
                            // Merge-Logik: Behalte alte Medien, wenn
                            // neue leer sind
                            val mergedGame =
                                apiGame.copy(
                                    screenshots =
                                        apiGame.screenshots
                                            .ifEmpty {
                                                fav.screenshots
                                            },
                                    movies =
                                        apiGame.movies
                                            .ifEmpty {
                                                fav.movies
                                            }
                                )
                            favoriteGameDao.insertFavorite(
                                mergedGame.toFavoriteEntity()
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                /* Fehler ignorieren, nächster Favorit */
            }
        }
    }

    suspend fun exportFavoritesToUri(context: Context, uri: Uri): Result<Unit> =
        try {
            val favorites = getAllFavorites().first()
            val type = Types.newParameterizedType(List::class.java, Game::class.java)
            val adapter = moshi.adapter<List<Game>>(type)
            val json = adapter.toJson(favorites)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }
                ?: throw Exception("Konnte OutputStream nicht öffnen")
            Result.success(Unit)
        } catch (e: Exception) {
            CrashlyticsHelper.recordException(e)
            AppLogger.e(
                "FavoritesRepository",
                "Fehler beim Export (Uri): ${e.localizedMessage}",
                e
            )
            Result.failure(e)
        }

    suspend fun importFavoritesFromUri(context: Context, uri: Uri): Result<Unit> =
        try {
            val type = Types.newParameterizedType(List::class.java, Game::class.java)
            val adapter = moshi.adapter<List<Game>>(type)
            val json =
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        reader.readText()
                    }
                }
                    ?: throw Exception("Konnte InputStream nicht öffnen")
            val games = adapter.fromJson(json) ?: emptyList()
            games.forEach { addFavorite(context, it) }
            Result.success(Unit)
        } catch (e: Exception) {
            CrashlyticsHelper.recordException(e)
            AppLogger.e(
                "FavoritesRepository",
                "Fehler beim Import (Uri): ${e.localizedMessage}",
                e
            )
            Result.failure(e)
        }
}
