package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.core.content.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.FavoriteGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import javax.inject.*

class GameRepository @Inject constructor(
    private val api: RawgApi,
    private val gameCacheDao: GameCacheDao,
    private val favoriteGameDao: FavoriteGameDao,
    private val context: Context,
) {

    suspend fun getGameDetail(gameId: Int): Resource<Game> {
        AppLogger.debug("GameRepository", "[DEBUG] getGameDetail() aufgerufen für ID: $gameId")
        AppLogger.info("GameRepository", "getGameDetail() aufgerufen für ID: $gameId")
        AppLogger.debug("GameRepository", "Lade Spieldetails für ID: $gameId")

        // NEU: Prüfe zuerst, ob das Spiel ein Favorit ist
        val favorite = favoriteGameDao.getFavoriteById(gameId)
        if (favorite != null) {
            AppLogger.debug(
                "GameRepository",
                "[DEBUG] Favorit gefunden für $gameId – lade aus Favoriten-Tabelle"
            )
            AppLogger.info(
                "GameRepository",
                "Favorit gefunden für $gameId – lade aus Favoriten-Tabelle"
            )
            return Resource.Success(favorite.toGame())
        }

        // Prüfe zuerst den Cache
        val cachedGame = gameCacheDao.getGameById(gameId)
        AppLogger.debug(
            "GameRepository",
            "[DEBUG] cachedGame: ${cachedGame != null}, Screenshots: ${cachedGame?.toGame()?.screenshots?.size ?: 0}"
        )
        if (cachedGame != null && NetworkUtils.isCacheValid(cachedGame.cachedAt)) {
            AppLogger.debug("GameRepository", "[DEBUG] Gültiger Cache gefunden für $gameId")
            AppLogger.info("GameRepository", "Gültiger Cache gefunden für $gameId")
            val game = cachedGame.toGame()
            AppLogger.debug("GameRepository", "Gecachte Screenshots: ${game.screenshots.size}")
            AppLogger.debug("GameRepository", "Gecachte Website: '${game.website}'")
            return Resource.Success(game)
        }

        // Wenn kein Netzwerk verfügbar und Cache vorhanden, verwende Cache auch wenn abgelaufen
        if (!NetworkUtils.isNetworkAvailable(context)) {
            AppLogger.debug(
                "GameRepository",
                "[DEBUG] Kein Netzwerk, prüfe Offline-Cache für $gameId"
            )
            AppLogger.info("GameRepository", "Kein Netzwerk verfügbar für $gameId")
            return if (cachedGame != null) {
                val game = cachedGame.toGame()
                AppLogger.debug(
                    "GameRepository",
                    "Offline-Cache Screenshots: ${game.screenshots.size}"
                )
                Resource.Success(game)
            } else {
                Resource.Error("Keine Internetverbindung und keine gecachten Daten verfügbar")
            }
        }

        return try {
            AppLogger.debug("GameRepository", "[DEBUG] Starte API-Call für Spieldetails $gameId")
            AppLogger.info("GameRepository", "Starte API-Call für Spieldetails $gameId")
            val resp = api.getGameDetail(gameId)
            
            if (resp.isSuccessful) {
                AppLogger.info("GameRepository", "API-Call erfolgreich für $gameId")
                resp.body()?.let { gameDto ->
                    AppLogger.debug(
                        "GameRepository",
                        "[DEBUG] API-Response erhalten: ${gameDto.name}"
                    )
                    AppLogger.debug(
                        "GameRepository",
                        "[DEBUG] API-Response Screenshots: ${gameDto.shortScreenshots?.size ?: 0}"
                    )
                    AppLogger.debug(
                        "GameRepository",
                        "API Website: '${gameDto.website}' (Typ: ${if (gameDto.website == null) "null" else "'${gameDto.website}'"})"
                    )
                    gameDto.shortScreenshots?.forEachIndexed { index, screenshot ->
                        AppLogger.debug(
                            "GameRepository",
                            "API Screenshot $index: ${screenshot.image}"
                        )
                    }

                    // Lade Screenshots separat, da sie nicht automatisch mitgeliefert werden
                    val screenshots = try {
                        AppLogger.debug(
                            "GameRepository",
                            "[DEBUG] Lade separate Screenshots für $gameId"
                        )
                        val screenshotsResp = api.getGameScreenshots(gameId, BuildConfig.API_KEY)
                        if (screenshotsResp.isSuccessful) {
                            val screenshotResponse = screenshotsResp.body()
                            screenshotResponse?.results?.map { it.image } ?: emptyList()
                        } else {
                            AppLogger.warn(
                                "GameRepository",
                                "Fehler beim Laden der Screenshots: ${screenshotsResp.code()}"
                            )
                            emptyList()
                        }
                    } catch (e: Exception) {
                        AppLogger.error(
                            "GameRepository",
                            "[ERROR] Exception beim Laden der Screenshots",
                            e
                        )
                        emptyList()
                    }

                    AppLogger.debug(
                        "GameRepository",
                        "[DEBUG] Separate Screenshots geladen: ${screenshots.size}"
                    )

                    // Lade Movies/Trailer separat
                    val movies = try {
                        AppLogger.debug(
                            "GameRepository",
                            "[DEBUG] Lade separate Movies für $gameId"
                        )
                        val moviesResp = api.getGameMovies(gameId, BuildConfig.API_KEY)
                        if (moviesResp.isSuccessful) {
                            val movieResponse = moviesResp.body()
                            movieResponse?.results?.map { it.toDomain() } ?: emptyList()
                        } else {
                            AppLogger.warn(
                                "GameRepository",
                                "Fehler beim Laden der Movies: ${moviesResp.code()}"
                            )
                            emptyList()
                        }
                    } catch (e: Exception) {
                        AppLogger.error(
                            "GameRepository",
                            "[ERROR] Exception beim Laden der Movies",
                            e
                        )
                        emptyList()
                    }

                    AppLogger.debug(
                        "GameRepository",
                        "[DEBUG] Separate Movies geladen: ${movies.size}"
                    )

                    val game = gameDto.toDomain().copy(
                        screenshots = screenshots,
                        movies = movies
                    )
                    AppLogger.debug(
                        "GameRepository",
                        "Konvertiert zu Domain: ${game.screenshots.size} Screenshots"
                    )
                    AppLogger.debug(
                        "GameRepository",
                        "Domain Website: '${game.website}' (Typ: ${if (game.website == null) "null" else "'${game.website}'"})"
                    )

                    // Cache das Spiel
                    val cachedEntity = gameCacheDao.getGameById(game.id)
                    val cachedGame = cachedEntity?.toGame()
                    val mergedGame = if (cachedGame != null) {
                        game.copy(
                            title = if (game.title.isNotBlank()) game.title else cachedGame.title,
                            releaseDate = game.releaseDate ?: cachedGame.releaseDate,
                            imageUrl = game.imageUrl ?: cachedGame.imageUrl,
                            rating = if (game.rating != 0f) game.rating else cachedGame.rating,
                            description = if (!game.description.isNullOrBlank()) game.description else cachedGame.description,
                            metacritic = game.metacritic ?: cachedGame.metacritic,
                            website = if (!game.website.isNullOrBlank()) game.website else cachedGame.website,
                            esrbRating = if (!game.esrbRating.isNullOrBlank()) game.esrbRating else cachedGame.esrbRating,
                            genres = if (game.genres.isNotEmpty()) game.genres else cachedGame.genres,
                            platforms = if (game.platforms.isNotEmpty()) game.platforms else cachedGame.platforms,
                            developers = if (game.developers.isNotEmpty()) game.developers else cachedGame.developers,
                            publishers = if (game.publishers.isNotEmpty()) game.publishers else cachedGame.publishers,
                            tags = if (game.tags.isNotEmpty()) game.tags else cachedGame.tags,
                            screenshots = if (game.screenshots.isNotEmpty()) game.screenshots else cachedGame.screenshots,
                            stores = if (game.stores.isNotEmpty()) game.stores else cachedGame.stores,
                            playtime = game.playtime ?: cachedGame.playtime,
                            movies = if (game.movies.isNotEmpty()) game.movies else cachedGame.movies
                        )
                    } else game
                    // Nur speichern, wenn sich etwas geändert hat
                    if (cachedGame == null || mergedGame != cachedGame) {
                        gameCacheDao.insertGame(mergedGame.toCacheEntity())
                    }

                    AppLogger.info(
                        "GameRepository",
                        "Spiel wird gecacht: ${mergedGame.screenshots.size} Screenshots für $gameId"
                    )

                    Resource.Success(mergedGame)
                } ?: Resource.Error("Leere Antwort von der API")
            } else {
                AppLogger.error("GameRepository", "[ERROR] API-Fehler: ${resp.code()} für $gameId")
                AppLogger.info("GameRepository", "API-Fehler: ${resp.code()} für $gameId")
                // Fallback auf Cache wenn API fehlschlägt
                if (cachedGame != null) {
                    val game = cachedGame.toGame()
                    AppLogger.debug(
                        "GameRepository",
                        "Fallback-Cache Screenshots: ${game.screenshots.size}"
                    )
                    Resource.Success(game)
                } else {
                    Resource.Error("Server Error ${resp.code()}")
                }
            }
        } catch (e: Exception) {
            AppLogger.error("GameRepository", "Netzwerkfehler", e)
            AppLogger.info("GameRepository", "Netzwerkfehler für $gameId: ${e.localizedMessage}")
            // Fallback auf Cache bei Netzwerkfehler
            if (cachedGame != null) {
                val game = cachedGame.toGame()
                AppLogger.debug(
                    "GameRepository",
                    "Error-Cache Screenshots: ${game.screenshots.size}"
                )
                Resource.Success(game)
            } else {
                Resource.Error(
                    ErrorHandler.handleException(
                        e,
                        "Netzwerkfehler: ${e.localizedMessage}"
                    )
                )
            }
        }
        AppLogger.debug("GameRepository", "[DEBUG] getGameDetail() fertig für $gameId")
    }

    suspend fun getPlatforms(): Resource<List<Platform>> {
        return try {
            val response = api.getPlatforms()
            if (response.isSuccessful) {
                response.body()?.let { platformResponse ->
                    val platforms = platformResponse.platforms.map { platformDto ->
                        Platform(
                            id = platformDto.id,
                            name = platformDto.name
                        )
                    }
                    Resource.Success(platforms)
                } ?: Resource.Error("Keine Plattform-Daten erhalten")
            } else {
                Resource.Error("API-Fehler: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(ErrorHandler.handleException(e, "Netzwerkfehler: ${e.localizedMessage}"))
        }
    }

    suspend fun getGenres(): Resource<List<Genre>> {
        return try {
            val response = api.getGenres()
            if (response.isSuccessful) {
                response.body()?.let { genreResponse ->
                    val genres = genreResponse.genres.map { genreDto ->
                        Genre(
                            id = genreDto.id,
                            name = genreDto.name
                        )
                    }
                    Resource.Success(genres)
                } ?: Resource.Error("Keine Genre-Daten erhalten")
            } else {
                Resource.Error("API-Fehler: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(ErrorHandler.handleException(e, "Netzwerkfehler: ${e.localizedMessage}"))
        }
    }

    // PagingSource für die Suche mit Offline-Support
    private inner class GamePagingSource(
        private val query: String,
        private val platforms: String?,
        private val genres: String?,
        private val ordering: String?,
        private val rating: Float?,
    ) : PagingSource<Int, Game>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
            val page = params.key ?: 1
            val filterHash = NetworkUtils.createFilterHash(platforms, genres, ordering, rating)
            
            // Prüfe Cache für erste Seite
            if (page == 1) {
                val cachedGames = gameCacheDao.getGamesByQueryAndFilter(query, filterHash).first()
                if (cachedGames.isNotEmpty() && NetworkUtils.isCacheValid(cachedGames.first().cachedAt)) {
                    val filteredGames = if (rating != null && rating > 0f) {
                        cachedGames.map { it.toGame() }.filter { it.rating >= rating }
                    } else {
                        cachedGames.map { it.toGame() }
                    }
                    return LoadResult.Page(
                        data = filteredGames,
                        prevKey = null,
                        nextKey = if (filteredGames.size >= params.loadSize) 2 else null
                    )
                }
            }
            
            // Wenn kein Netzwerk verfügbar, verwende Cache auch wenn abgelaufen
            if (!NetworkUtils.isNetworkAvailable(context)) {
                val cachedGames = gameCacheDao.getGamesByQueryAndFilter(query, filterHash).first()
                if (cachedGames.isNotEmpty()) {
                    val filteredGames = if (rating != null && rating > 0f) {
                        cachedGames.map { it.toGame() }.filter { it.rating >= rating }
                    } else {
                        cachedGames.map { it.toGame() }
                    }
                    return LoadResult.Page(
                        data = filteredGames,
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    return LoadResult.Error(Exception("Keine Internetverbindung und keine gecachten Daten verfügbar"))
                }
            }
            
            // API-Call
            return try {
                val resp = api.searchGames(
                    query = query,
                    platforms = platforms,
                    genres = genres,
                    ordering = ordering,
                    page = page,
                    pageSize = params.loadSize
                )
                if (resp.isSuccessful) {
                    val body = resp.body()
                    val allGames = body?.results?.map { it.toDomain() } ?: emptyList()
                    val filteredGames = if (rating != null && rating > 0f) {
                        allGames.filter { it.rating >= rating }
                    } else {
                        allGames
                    }
                    
                    // Speichere in Cache (nur erste Seite)
                    if (page == 1) {
                        val cacheEntities = allGames.map { game ->
                            game.toCacheEntity(query, filterHash)
                        }
                        gameCacheDao.insertGames(cacheEntities)
                    }
                    
                    val nextPage = if (body?.next != null) page + 1 else null
                    LoadResult.Page(
                        data = filteredGames,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextPage
                    )
                } else {
                    // Fallback auf Cache
                    val cachedGames = gameCacheDao.getGamesByQueryAndFilter(query, filterHash).first()
                    if (cachedGames.isNotEmpty()) {
                        val filteredGames = if (rating != null && rating > 0f) {
                            cachedGames.map { it.toGame() }.filter { it.rating >= rating }
                        } else {
                            cachedGames.map { it.toGame() }
                        }
                        LoadResult.Page(
                            data = filteredGames,
                            prevKey = null,
                            nextKey = null
                        )
                    } else {
                        LoadResult.Error(Exception("Server Error"))
                    }
                }
            } catch (e: Exception) {
                AppLogger.error("GameRepository", "Fehler beim API-Call in PagingSource", e)
                // Fallback auf Cache bei Netzwerkfehler
                val cachedGames = gameCacheDao.getGamesByQueryAndFilter(query, filterHash).first()
                if (cachedGames.isNotEmpty()) {
                    val filteredGames = if (rating != null && rating > 0f) {
                        cachedGames.map { it.toGame() }.filter { it.rating >= rating }
                    } else {
                        cachedGames.map { it.toGame() }
                    }
                    LoadResult.Page(
                        data = filteredGames,
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    LoadResult.Error(
                        Exception(
                            ErrorHandler.handleException(
                                e,
                                "Server Error: ${e.localizedMessage}"
                            )
                        )
                    )
                }
            }
        }
        
        override fun getRefreshKey(state: PagingState<Int, Game>): Int? = 1
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
            pagingSourceFactory = { GamePagingSource(query, platforms, genres, ordering, rating) }
        ).flow
    }
    
    /**
     * Cache verwalten
     */
    suspend fun clearCache() {
        AppLogger.debug("GameRepository", "Lösche gesamten Cache")
        gameCacheDao.clearAllGames()
    }
    
    suspend fun getCacheSize(): Int {
        return gameCacheDao.getCacheSize()
    }

    suspend fun getGameFromCache(id: Int): Game? {
        val entity = gameCacheDao.getGameById(id)
        return entity?.toGame()
    }
    
    /**
     * Cache optimieren - entferne alte Einträge
     */
    suspend fun optimizeCache() {
        CacheUtils.optimizeCache(gameCacheDao)
    }
    
    /**
     * Cache-Statistiken abrufen
     */
    suspend fun getCacheStats(): CacheStats {
        return CacheUtils.getCacheStats(gameCacheDao)
    }

    /**
     * Sucht die GameId anhand eines Slugs. Erst im Cache, dann in Favoriten, dann per API.
     */
    suspend fun getGameIdBySlug(slug: String): Int? {
        // 1. Suche im Cache
        val cachedGames = gameCacheDao.getAllCachedGames().firstOrNull() ?: emptyList()
        cachedGames.forEach { entity ->
            if (entity.slug == slug) return entity.id
        }
        // 2. Suche in Favoriten
        val favoriteGames = favoriteGameDao.getAllFavorites().firstOrNull() ?: emptyList()
        favoriteGames.forEach { entity ->
            if (entity.slug == slug) return entity.id
        }
        // 3. Suche per API (RAWG unterstützt Suche nach Slug nicht direkt, aber als Fallback kann man die Such-API nutzen)
        return try {
            val response = api.searchGames(query = slug)
            if (response.isSuccessful) {
                val game = response.body()?.results?.find { it.slug == slug }
                game?.id
            } else null
        } catch (e: Exception) {
            AppLogger.error("GameRepository", "Fehler beim Suchen der GameId per API", e)
            null
        }
    }

    /**
     * Vergleicht die neuesten Spiele (nach ID und Slug) mit den lokal gespeicherten Werten.
     * Gibt alle neuen Spiele zurück und aktualisiert die gespeicherten Werte.
     */
    suspend fun checkForNewGamesAndUpdatePrefs(
        prefs: SharedPreferences,
        count: Int = 10,
    ): List<Game> {
        // 1. Hole die letzten Spiele von der API
        val response = api.searchGames(page = 1, pageSize = count, query = "")
        if (!response.isSuccessful) return emptyList()
        val games = response.body()?.results?.map { it.toDomain() } ?: return emptyList()

        // 2. Lade die letzten bekannten IDs/Slugs aus den SharedPreferences
        val lastIds = prefs.getStringSet("last_known_game_ids", emptySet()) ?: emptySet()
        val lastSlugs = prefs.getStringSet("last_known_game_slugs", emptySet()) ?: emptySet()

        // 3. Finde neue Spiele (ID oder Slug noch nicht bekannt)
        val newGames = games.filter { it.id.toString() !in lastIds || it.slug !in lastSlugs }

        // 4. Aktualisiere die gespeicherten IDs/Slugs (nur die neuesten)
        prefs.edit {
            putStringSet("last_known_game_ids", games.map { it.id.toString() }.toSet())
                .putStringSet("last_known_game_slugs", games.map { it.slug }.toSet())
        }

        return newGames
    }
}

/**
 * Cache-Statistiken
 */
data class CacheStats(
    val totalEntries: Int,
    val oldestEntryTime: Long?,
    val isExpired: Boolean,
)