package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.core.content.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameDetailCacheMapper.toDetailCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameDetailCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import javax.inject.*

class GameRepository
@Inject
constructor(
    private val api: RawgApi,
    private val gameCacheDao: GameCacheDao,
    private val favoriteGameDao: FavoriteGameDao,
    private val gameDetailCacheDao: GameDetailCacheDao,
    private val context: Context,
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("game_repo_prefs", Context.MODE_PRIVATE)

    private val lastSyncTime = Constants.LAST_SYNC_TIME

    suspend fun getGameDetail(gameId: Int): Resource<Game> {
        PerformanceMonitor.startTimer("api_getGameDetail")
        // Setze Custom Key für besseres Crashlytics-Tracking
        CrashlyticsHelper.setCustomKey("current_game_id", gameId)
        CrashlyticsHelper.setCustomKey("operation", "get_game_detail")

        AppLogger.d("GameRepository", "[DEBUG] getGameDetail() aufgerufen für ID: $gameId")
        AppLogger.i("GameRepository", "getGameDetail() aufgerufen für ID: $gameId")
        AppLogger.d("GameRepository", "Lade Spieldetails für ID: $gameId")

        // NEU: Prüfe zuerst, ob das Spiel ein Favorit ist
        val favorite = favoriteGameDao.getFavoriteById(gameId)
        if (favorite != null) {
            CrashlyticsHelper.setCustomKey("data_source", "favorites")
            AppLogger.d(
                "GameRepository",
                "[DEBUG] Favorit gefunden für $gameId – lade aus Favoriten-Tabelle"
            )
            AppLogger.i(
                "GameRepository",
                "Favorit gefunden für $gameId – lade aus Favoriten-Tabelle"
            )
            PerformanceMonitor.endTimer("api_getGameDetail")
            return Resource.Success(favorite.toGame())
        }

        // Prüfe zuerst den Detail-Cache
        val cachedDetail = gameDetailCacheDao.getGameDetailById(gameId)
        AppLogger.d(
            "GameRepository",
            "[DEBUG] cachedDetail: ${cachedDetail != null}, Screenshots: ${cachedDetail?.toGame()?.screenshots?.size ?: 0}"
        )
        if (cachedDetail != null && NetworkUtils.isCacheValid(cachedDetail.detailCachedAt)) {
            CrashlyticsHelper.setCustomKey("data_source", "detail_cache")
            CrashlyticsHelper.setCustomKey(
                "cache_age_hours",
                ((System.currentTimeMillis() - cachedDetail.detailCachedAt) / (1000 * 60 * 60))
                    .toInt()
            )
            AppLogger.d("GameRepository", "[DEBUG] Gültiger Detail-Cache gefunden für $gameId")
            AppLogger.i("GameRepository", "Gültiger Detail-Cache gefunden für $gameId")
            val game = cachedDetail.toGame()
            AppLogger.d("GameRepository", "Gecachte Screenshots: ${game.screenshots.size}")
            AppLogger.d("GameRepository", "Gecachte Website: '${game.website}'")
            PerformanceMonitor.endTimer("api_getGameDetail")
            return Resource.Success(game)
        }

        // Wenn kein Netzwerk verfügbar und Detail-Cache vorhanden, verwende Cache auch wenn
        // abgelaufen
        if (!NetworkUtils.isNetworkAvailable(context)) {
            CrashlyticsHelper.setCustomKey("network_available", false)
            CrashlyticsHelper.setCustomKey("data_source", "offline_cache")
            AppLogger.d(
                "GameRepository",
                "[DEBUG] Kein Netzwerk, prüfe Offline-Detail-Cache für $gameId"
            )
            AppLogger.i("GameRepository", "Kein Netzwerk verfügbar für $gameId")
            PerformanceMonitor.endTimer("api_getGameDetail")
            return if (cachedDetail != null) {
                val game = cachedDetail.toGame()
                AppLogger.d(
                    "GameRepository",
                    "Offline-Detail-Cache Screenshots: ${game.screenshots.size}"
                )
                Resource.Success(game)
            } else {
                Resource.Error(
                    "Fehler beim Laden der Screenshots: Kein Netzwerk und kein Cache verfügbar"
                )
            }
        }

        val result =
            try {
                CrashlyticsHelper.setCustomKey("data_source", "api")
                CrashlyticsHelper.setCustomKey("network_available", true)
                AppLogger.d(
                    "GameRepository",
                    "[DEBUG] Starte API-Call für Spieldetails $gameId"
                )
                AppLogger.i("GameRepository", "Starte API-Call für Spieldetails $gameId")
                val resp = api.getGameDetail(gameId)

                if (resp.isSuccessful) {
                    AppLogger.i("GameRepository", "API-Call erfolgreich für $gameId")
                    resp.body()?.let { gameDto ->
                        AppLogger.d(
                            "GameRepository",
                            "[DEBUG] API-Response erhalten: ${gameDto.name}"
                        )
                        AppLogger.d(
                            "GameRepository",
                            "[DEBUG] API-Response Screenshots: ${gameDto.shortScreenshots?.size ?: 0}"
                        )
                        AppLogger.d(
                            "GameRepository",
                            "API Website: '${gameDto.website}' (Typ: ${if (gameDto.website == null) "null" else "'${gameDto.website}'"})"
                        )
                        gameDto.shortScreenshots?.forEachIndexed { index, screenshot ->
                            AppLogger.d(
                                "GameRepository",
                                "API Screenshot $index: ${screenshot.image}"
                            )
                        }

                        // Lade Screenshots separat, da sie nicht automatisch mitgeliefert
                        // werden
                        val screenshots =
                            try {
                                AppLogger.d(
                                    "GameRepository",
                                    "[DEBUG] Lade separate Screenshots für $gameId"
                                )
                                val screenshotsResp =
                                    api.getGameScreenshots(gameId, BuildConfig.API_KEY)
                                if (screenshotsResp.isSuccessful) {
                                    val screenshotResponse = screenshotsResp.body()
                                    screenshotResponse?.results?.map { it.image }
                                        ?: emptyList()
                                } else {
                                    CrashlyticsHelper.setCustomKey(
                                        "screenshots_error_code",
                                        screenshotsResp.code()
                                    )
                                    AppLogger.w(
                                        "GameRepository",
                                        "Fehler beim Laden der Screenshots: ${screenshotsResp.code()}"
                                    )
                                    emptyList()
                                }
                            } catch (e: Exception) {
                                CrashlyticsHelper.setCustomKey(
                                    "screenshots_error",
                                    e.javaClass.simpleName
                                )
                                AppLogger.e(
                                    "GameRepository",
                                    "[ERROR] Exception beim Laden der Screenshots",
                                    e
                                )
                                emptyList()
                            }

                        AppLogger.d(
                            "GameRepository",
                            "[DEBUG] Separate Screenshots geladen: ${screenshots.size}"
                        )

                        // Lade Movies/Trailer separat
                        val movies =
                            try {
                                AppLogger.d(
                                    "GameRepository",
                                    "[DEBUG] Lade separate Movies für $gameId"
                                )
                                val moviesResp =
                                    api.getGameMovies(gameId, BuildConfig.API_KEY)
                                if (moviesResp.isSuccessful) {
                                    val movieResponse = moviesResp.body()
                                    val movieList =
                                        movieResponse?.results?.map { it.toDomain() }
                                            ?: emptyList()
                                    AppLogger.d(
                                        "GameRepository",
                                        "[DEBUG] Movies API Response: ${movieResponse?.count ?: 0} Movies gefunden"
                                    )
                                    AppLogger.d(
                                        "GameRepository",
                                        "[DEBUG] Movies konvertiert: ${movieList.size} Movies"
                                    )
                                    movieList.forEachIndexed { index, movie ->
                                        AppLogger.d(
                                            "GameRepository",
                                            "[DEBUG] Movie $index: ${movie.name} (ID: ${movie.id})"
                                        )
                                    }
                                    movieList
                                } else {
                                    CrashlyticsHelper.setCustomKey(
                                        "movies_error_code",
                                        moviesResp.code()
                                    )
                                    AppLogger.w(
                                        "GameRepository",
                                        "Fehler beim Laden der Movies: ${moviesResp.code()}"
                                    )
                                    emptyList()
                                }
                            } catch (e: Exception) {
                                CrashlyticsHelper.setCustomKey(
                                    "movies_error",
                                    e.javaClass.simpleName
                                )
                                AppLogger.e(
                                    "GameRepository",
                                    "[ERROR] Exception beim Laden der Movies",
                                    e
                                )
                                emptyList()
                            }

                        AppLogger.d(
                            "GameRepository",
                            "[DEBUG] Separate Movies geladen: ${movies.size}"
                        )

                        // Prüfe, ob bereits Screenshots und Movies im Detail-Cache vorhanden
                        // sind
                        val existingScreenshots =
                            cachedDetail?.toGame()?.screenshots ?: emptyList()
                        val existingMovies = cachedDetail?.toGame()?.movies ?: emptyList()

                        val game =
                            gameDto.toDomain()
                                .copy(
                                    screenshots =
                                        screenshots.ifEmpty {
                                            existingScreenshots
                                        },
                                    movies = movies.ifEmpty { existingMovies }
                                )
                        // Cache das Spiel im Detail-Cache
                        val cachedGame = cachedDetail?.toGame()
                        val mergedGame =
                            if (cachedGame != null) {
                                game.copy(
                                    screenshots =
                                        game.screenshots.ifEmpty {
                                            cachedGame.screenshots
                                        },
                                    movies = game.movies.ifEmpty { cachedGame.movies }
                                )
                            } else {
                                game
                            }
                        // Nur speichern, wenn sich etwas geändert hat
                        if (cachedGame == null || mergedGame != cachedGame) {
                            try {
                                AppLogger.d(
                                    "GameRepository",
                                    "Versuche Detail-Spiel zu cachen: ${mergedGame.title} (ID: ${mergedGame.id})"
                                )
                                gameDetailCacheDao.insertGameDetail(
                                    mergedGame.toDetailCacheEntity()
                                )
                                AppLogger.d(
                                    "GameRepository",
                                    "Detail-Spiel gecacht: ${mergedGame.title} (ID: ${mergedGame.id})"
                                )
                            } catch (e: Exception) {
                                CrashlyticsHelper.setCustomKey(
                                    "cache_error",
                                    e.javaClass.simpleName
                                )
                                AppLogger.e(
                                    "GameRepository",
                                    "Fehler beim Cachen im Detail-Cache: ${e.localizedMessage}",
                                    e
                                )
                            }
                        }
                        AppLogger.i(
                            "GameRepository",
                            "Detail-Spiel wird gecacht: ${mergedGame.screenshots.size} Screenshots für $gameId"
                        )

                        Resource.Success(mergedGame)
                    }
                        ?: Resource.Error("Leere Antwort von der API")
                } else {
                    CrashlyticsHelper.setCustomKey("api_error_code", resp.code())
                    AppLogger.e(
                        "GameRepository",
                        "[ERROR] API-Fehler: ${resp.code()} für $gameId"
                    )
                    AppLogger.i("GameRepository", "API-Fehler: ${resp.code()} für $gameId")
                    // Fallback auf Detail-Cache wenn API fehlschlägt
                    if (cachedDetail != null) {
                        CrashlyticsHelper.setCustomKey("fallback_to_cache", true)
                        val game = cachedDetail.toGame()
                        AppLogger.d(
                            "GameRepository",
                            "Fallback-Detail-Cache Screenshots: ${game.screenshots.size}"
                        )
                        Resource.Success(game)
                    } else {
                        Resource.Error(
                            "Fehler beim Laden der Screenshots: Serverfehler ${resp.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.setCustomKey("exception_type", e.javaClass.simpleName)
                CrashlyticsHelper.setCustomKey("exception_message", e.message ?: "Unknown")
                CrashlyticsHelper.recordException(e)
                AppLogger.e("GameRepository", "Netzwerkfehler", e)
                AppLogger.i(
                    "GameRepository",
                    "Netzwerkfehler für $gameId: ${e.localizedMessage}"
                )
                // Fallback auf Detail-Cache bei Netzwerkfehler
                if (cachedDetail != null) {
                    CrashlyticsHelper.setCustomKey("fallback_to_cache", true)
                    val game = cachedDetail.toGame()
                    AppLogger.d(
                        "GameRepository",
                        "Error-Detail-Cache Screenshots: ${game.screenshots.size}"
                    )
                    Resource.Success(game)
                } else {
                    CrashlyticsHelper.recordException(e)
                    Resource.Error(
                        handleException(
                            e,
                            "Fehler beim Laden der Screenshots: Netzwerkfehler ${e.localizedMessage}"
                        )
                    )
                }
            }
        PerformanceMonitor.endTimer("api_getGameDetail")
        return result
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
                    ?: return Resource.Error(
                        "Fehler beim Laden der Plattformen: Keine Plattformdaten verfügbar"
                    )
            } else {
                CrashlyticsHelper.setCustomKey("platforms_error_code", response.code())
                return Resource.Error(
                    "Fehler beim Laden der Plattformen: API-Fehler ${response.code()}"
                )
            }
        } catch (e: Exception) {
            CrashlyticsHelper.setCustomKey("platforms_exception", e.javaClass.simpleName)
            return Resource.Error(
                handleException(
                    e,
                    "Fehler beim Laden der Plattformen: Netzwerkfehler ${e.localizedMessage}"
                )
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
                    ?: return Resource.Error(
                        "Fehler beim Laden der Genres: Keine Genres verfügbar"
                    )
            } else {
                CrashlyticsHelper.setCustomKey("genres_error_code", response.code())
                return Resource.Error("Fehler beim Laden der Genres: API-Fehler ${response.code()}")
            }
        } catch (e: Exception) {
            CrashlyticsHelper.setCustomKey("genres_exception", e.javaClass.simpleName)
            return Resource.Error(
                handleException(
                    e,
                    "Fehler beim Laden der Genres: Netzwerkfehler ${e.localizedMessage}"
                )
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

    /** Cache optimieren - entferne alte Einträge */
    suspend fun optimizeCache() {
        CacheUtils.optimizeCache(gameCacheDao)
        setLastSyncTime(System.currentTimeMillis())
    }

    /** Cache-Statistiken abrufen */
    suspend fun getCacheStats(): CacheStats {
        return CacheUtils.getCacheStats(gameCacheDao)
    }

    fun getLastSyncTime(): Long? {
        val value = prefs.getLong(lastSyncTime, -1L)
        return if (value > 0) value else null
    }

    private fun setLastSyncTime(timestamp: Long) {
        prefs.edit { putLong(lastSyncTime, timestamp) }
    }

    /** Sucht die GameId anhand eines Slugs. Erst im Cache, dann in Favoriten, dann per API. */
    suspend fun getGameIdBySlug(slug: String): Int? {
        // 1. Suche im Cache
        val cachedGames = gameCacheDao.getAllCachedGames().firstOrNull() ?: emptyList()
        cachedGames.forEach { entity -> if (entity.slug == slug) return entity.id }
        // 2. Suche in Favoriten
        val favoriteGames = favoriteGameDao.getAllFavorites().firstOrNull() ?: emptyList()
        favoriteGames.forEach { entity -> if (entity.slug == slug) return entity.id }
        // 3. Suche per API (RAWG unterstützt Suche nach Slug nicht direkt, aber als Fallback kann
        // man die Such-API nutzen)
        return try {
            val response = api.searchGames(query = slug)
            if (response.isSuccessful) {
                val game = response.body()?.results?.find { it.slug == slug }
                game?.id
            } else null
        } catch (e: Exception) {
            AppLogger.e("GameRepository", "Fehler beim Suchen der GameId per API", e)
            null
        }
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
            is java.net.UnknownHostException -> "Keine Internetverbindung verfügbar"
            is java.net.SocketTimeoutException -> "Zeitüberschreitung bei der Verbindung"
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    404 -> "Daten nicht gefunden"
                    500 -> "Serverfehler - bitte versuchen Sie es später erneut"
                    503 -> "Service temporär nicht verfügbar"
                    else -> "Serverfehler (${exception.code()})"
                }
            }
            is java.io.IOException -> "Netzwerkfehler - überprüfen Sie Ihre Verbindung"
            else -> defaultMessage
        }
    }
}
