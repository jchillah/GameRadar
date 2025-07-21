package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.core.content.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameDetailCacheMapper.toDetailCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameDetailCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.domain.models.Platform
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import retrofit2.*
import java.io.*
import java.net.*
import javax.inject.*

class GameRepository
@Inject
constructor(
    private val api: RawgApi,
    private val gameCacheDao: GameCacheDao,
    private val gameDetailCacheDao: GameDetailCacheDao,
    private val context: Context,
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("game_repo_prefs", Context.MODE_PRIVATE)

    private val lastSyncTimeKey = Constants.LAST_SYNC_TIME

    suspend fun getGameDetail(gameId: Int): Resource<Game> {
        return try {
            PerformanceMonitor.startTimer("game_detail_api_call")
            AppLogger.d("GameRepository", "Lade Spieldetails für Game ID: $gameId")

            // Prüfe zuerst den Cache
            val cachedGame = gameDetailCacheDao.getGameDetailById(gameId)
            if (cachedGame != null) {
                val game = cachedGame.toGame()
                val apiDuration = PerformanceMonitor.endTimer("game_detail_api_call")
                PerformanceMonitor.trackApiCall("game_detail_cache", apiDuration, true, 0)
                PerformanceMonitor.incrementEventCounter("game_detail_cache_hit")
                AppLogger.d("GameRepository", "Spieldetails aus Cache geladen für Game ID: $gameId")
                return Resource.Success(game)
            }

            // Lade Spieldetails, Movies und Screenshots parallel
            AppLogger.d("GameRepository", "Starte parallele API-Calls für Game ID: $gameId")
            val gameResponse = api.getGameDetail(gameId)
            val moviesResponse = api.getGameMovies(gameId)
            val screenshotsResponse = api.getGameScreenshots(gameId)

            // Logge Response-Status für alle API-Calls
            AppLogger.d("GameRepository", "Game Detail Response Code: ${gameResponse.code()}")
            AppLogger.d("GameRepository", "Movies Response Code: ${moviesResponse.code()}")
            AppLogger.d(
                "GameRepository",
                "Screenshots Response Code: ${screenshotsResponse.code()}"
            )

            if (gameResponse.isSuccessful) {
                val gameDto = gameResponse.body()
                if (gameDto != null) {
                    AppLogger.d(
                        "GameRepository",
                        "Game Detail erfolgreich geladen: ${gameDto.name}"
                    )

                    // Lade Movies, falls verfügbar
                    val movies =
                        if (moviesResponse.isSuccessful) {
                            val moviesBody = moviesResponse.body()
                            AppLogger.d("GameRepository", "Movies Response Body: $moviesBody")
                            val moviesList =
                                moviesBody?.results?.map { it.toDomain() } ?: emptyList()
                            AppLogger.d(
                                "GameRepository",
                                "Movies erfolgreich geladen: ${moviesList.size} Movies"
                            )
                            // Logge Details für jedes Movie
                            moviesList.forEach { movie ->
                                AppLogger.d(
                                    "GameRepository",
                                    "Movie: ${movie.name}, ID: ${movie.id}, Preview: ${
                                        movie.preview?.take(
                                            50
                                        )
                                    }..."
                                )
                            }
                            moviesList
                        } else {
                            AppLogger.w(
                                "GameRepository",
                                "Movies API fehlgeschlagen: ${moviesResponse.code()} - ${moviesResponse.message()}"
                            )
                            // Logge Response-Body für Debugging
                            try {
                                val errorBody = moviesResponse.errorBody()?.string()
                                    AppLogger.w(
                                        "GameRepository",
                                        "Movies API Error Body: $errorBody"
                                    )
                            } catch (e: Exception) {
                                AppLogger.w(
                                    "GameRepository",
                                    "Konnte Movies Error Body nicht lesen: ${e.message}"
                                )
                            }
                                emptyList()
                            }

                    // Lade Screenshots, falls verfügbar
                    val screenshots =
                        if (screenshotsResponse.isSuccessful) {
                            val screenshotsBody = screenshotsResponse.body()
                            val screenshotsList =
                                screenshotsBody?.results?.map { it.image } ?: emptyList()
                                AppLogger.d(
                                    "GameRepository",
                                    "Screenshots erfolgreich geladen: ${screenshotsList.size} Screenshots"
                                )
                            screenshotsList
                        } else {
                            AppLogger.w(
                                        "GameRepository",
                                "Screenshots API fehlgeschlagen: ${screenshotsResponse.code()} - ${screenshotsResponse.message()}"
                            )
                            emptyList()
                        }

                    // Logge Fallback-Screenshots
                    val fallbackScreenshots =
                        gameDto.shortScreenshots?.map { it.image } ?: emptyList()
                    AppLogger.d(
                        "GameRepository",
                        "Fallback Screenshots verfügbar: ${fallbackScreenshots.size}"
                    )

                    // Kombiniere Spieldaten mit Movies und Screenshots
                    val finalScreenshots = screenshots.ifEmpty { fallbackScreenshots }

                    AppLogger.d(
                            "GameRepository",
                        "Finale Screenshots: ${finalScreenshots.size}, Finale Movies: ${movies.size}"
                    )

                    val game =
                        gameDto.toDomain().copy(movies = movies, screenshots = finalScreenshots)

                    // Cache das Spiel
                    gameDetailCacheDao.insertGameDetail(game.toDetailCacheEntity())
                    AppLogger.d("GameRepository", "Spieldetails gecacht für Game ID: $gameId")

                    val apiDuration = PerformanceMonitor.endTimer("game_detail_api_call")
                    PerformanceMonitor.trackApiCall("game_detail", apiDuration, true, 0)
                    PerformanceMonitor.incrementEventCounter("game_detail_success")

                    Resource.Success(game)
                } else {
                    AppLogger.e(
                        "GameRepository",
                        "Game Detail Response Body ist null für Game ID: $gameId"
                    )
                    PerformanceMonitor.trackApiCall("game_detail", 0, false, 0)
                    PerformanceMonitor.incrementEventCounter("game_detail_empty_response")
                    Resource.Error("Spieldaten konnten nicht geladen werden")
                }
            } else {
                val errorMessage =
                    ErrorHandler.handle(HttpException(gameResponse), "GameRepository")
                AppLogger.e(
                        "GameRepository",
                    "Game Detail API fehlgeschlagen: ${gameResponse.code()} - ${gameResponse.message()}"
                )
                PerformanceMonitor.trackApiCall("game_detail", 0, false, 0)
                PerformanceMonitor.incrementEventCounter("game_detail_http_error")
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = ErrorHandler.handle(e, "GameRepository")
            AppLogger.e(
                "GameRepository",
                "Exception beim Laden der Spieldetails für Game ID: $gameId",
                e
            )
            PerformanceMonitor.trackApiCall("game_detail", 0, false, 0)
            PerformanceMonitor.incrementEventCounter("game_detail_exception")
            Resource.Error(errorMessage)
        }
    }

    suspend fun getPlatforms(): Resource<List<Platform>> {
        PerformanceMonitor.startTimer("api_getPlatforms")
        CrashlyticsHelper.setCustomKey("operation", "get_platforms")
        try {
            val response = api.getPlatforms()
            if (response.isSuccessful) {
                response.body()?.let { platformResponse ->
                    val platforms =
                        platformResponse.platforms.map { platformDto ->
                            Platform(id = platformDto.id, name = platformDto.name)
                        }
                    CrashlyticsHelper.setCustomKey("platforms_count", platforms.size)
                    return Resource.Success(platforms)
                }
                return Resource.Error(context.getString(R.string.error_platforms_no_data))
            } else {
                CrashlyticsHelper.setCustomKey("platforms_error_code", response.code())
                return Resource.Error(
                    context.getString(R.string.error_platforms_api, response.code())
                )
            }
        } catch (e: Exception) {
            CrashlyticsHelper.setCustomKey("platforms_exception", e.javaClass.simpleName)
            return Resource.Error(
                handleException(e, context.getString(R.string.error_network_check))
            )
        } finally {
            PerformanceMonitor.endTimer("api_getPlatforms")
        }
    }

    suspend fun getGenres(): Resource<List<Genre>> {
        PerformanceMonitor.startTimer("api_getGenres")
        CrashlyticsHelper.setCustomKey("operation", "get_genres")
        try {
            val response = api.getGenres()
            if (response.isSuccessful) {
                response.body()?.let { genreResponse ->
                    val genres =
                        genreResponse.genres.map { genreDto ->
                            Genre(id = genreDto.id, name = genreDto.name)
                        }
                    CrashlyticsHelper.setCustomKey("genres_count", genres.size)
                    return Resource.Success(genres)
                }
                return Resource.Error(context.getString(R.string.error_genres_no_data))
            } else {
                CrashlyticsHelper.setCustomKey("genres_error_code", response.code())
                return Resource.Error(context.getString(R.string.error_genres_api, response.code()))
            }
        } catch (e: Exception) {
            CrashlyticsHelper.setCustomKey("genres_exception", e.javaClass.simpleName)
            return Resource.Error(
                handleException(e, context.getString(R.string.error_network_check))
            )
        } finally {
            PerformanceMonitor.endTimer("api_getGenres")
        }
    }

    fun getPagedGames(
        query: String,
        platforms: String? = null,
        genres: String? = null,
        ordering: String? = null,
        rating: Float? = null,
    ): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                GamePagingSource(
                    api,
                    gameCacheDao,
                    context,
                    query,
                    platforms,
                    genres,
                    ordering,
                    rating
                )
            }
        )
            .flow
    }

    /** Cache verwalten */
    suspend fun clearCache() {
        AppLogger.d("GameRepository", "Lösche gesamten Cache")
        gameCacheDao.clearAllGames()
        setLastSyncTime(System.currentTimeMillis())
    }

    suspend fun getCacheSize(): Int {
        return gameCacheDao.getCacheSize()
    }

    /**
     * Cache optimieren - entferne alte Einträge.
     *
     * Entfernt abgelaufene Cache-Einträge und aktualisiert die Synchronisationszeit. Behandelt
     * Fehler mit Crashlytics-Integration für bessere Observability.
     *
     * Features:
     * - Automatische Bereinigung alter Cache-Einträge
     * - Aktualisierung der letzten Synchronisationszeit
     * - Robuste Fehlerbehandlung mit Crashlytics
     * - Logging für Debugging und Monitoring
     */
    suspend fun optimizeCache() {
        try {
            CrashlyticsHelper.setCustomKey("cache_optimize_attempted", true)
            CacheUtils.optimizeCache(gameCacheDao)
            setLastSyncTime(System.currentTimeMillis())
            CrashlyticsHelper.setCustomKey("cache_optimize_success", true)
            AppLogger.d("GameRepository", "Cache erfolgreich optimiert")
        } catch (e: Exception) {
            CrashlyticsHelper.setCustomKey("cache_optimize_error", true)
            CrashlyticsHelper.setCustomKey("cache_optimize_exception", e.javaClass.simpleName)
            AppLogger.e("GameRepository", "Fehler bei der Cache-Optimierung", e)

            // Crashlytics Error Recording
            CrashlyticsHelper.recordCacheError(
                "optimize_cache",
                gameCacheDao.getCacheSize(),
                e.message ?: "Unknown error"
            )
        }
    }

    /** Cache-Statistiken abrufen */
    suspend fun getCacheStats(): CacheStats {
        return CacheUtils.getCacheStats(gameCacheDao)
    }

    fun getLastSyncTime(): Long? {
        val value = prefs.getLong(lastSyncTimeKey, -1L)
        return if (value > 0) value else null
    }

    private fun setLastSyncTime(timestamp: Long) {
        prefs.edit { putLong(lastSyncTimeKey, timestamp) }
    }

    /**
     * Vergleicht die neuesten Spiele (nach ID und Slug) mit den lokal gespeicherten Werten. Gibt
     * alle neuen Spiele zurück und aktualisiert die gespeicherten Werte.
     */
    suspend fun checkForNewGamesAndUpdatePrefs(
        prefs: SharedPreferences,
        count: Int = 10,
    ): List<Game> {
        return checkForNewGamesAndUpdatePrefs(api, prefs, count)
    }

    /**
     * Behandelt Exceptions und gibt benutzerfreundliche Fehlermeldungen zurück. Folgt Clean Code
     * Best Practices für zentrale Fehlerbehandlung.
     */
    private fun handleException(exception: Exception, defaultMessage: String): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.error_no_connection)
            is SocketTimeoutException -> context.getString(R.string.error_timeout)
            is HttpException -> {
                when (exception.code()) {
                    404 -> context.getString(R.string.error_not_found)
                    500 -> context.getString(R.string.error_server_retry)
                    503 -> context.getString(R.string.error_service_unavailable)
                    else -> context.getString(R.string.error_server_code, exception.code())
                }
            }

            is IOException -> context.getString(R.string.error_network_check)
            else -> defaultMessage
        }
    }
}
