package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import android.util.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
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
        Log.d("GameRepository", "[DEBUG] getGameDetail() aufgerufen für ID: $gameId")
        Log.d("GameRepository", "Lade Spieldetails für ID: $gameId")

        // Prüfe zuerst den Cache
        val cachedGame = gameCacheDao.getGameById(gameId)
        Log.d(
            "GameRepository",
            "[DEBUG] cachedGame: ${cachedGame != null}, Screenshots: ${cachedGame?.toGame()?.screenshots?.size ?: 0}"
        )
        if (cachedGame != null && NetworkUtils.isCacheValid(cachedGame.cachedAt)) {
            Log.d("GameRepository", "[DEBUG] Gültiger Cache gefunden für $gameId")
            val game = cachedGame.toGame()
            Log.d("GameRepository", "Gecachte Screenshots: ${game.screenshots.size}")
            Log.d("GameRepository", "Gecachte Website: '${game.website}'")
            return Resource.Success(game)
        }

        // Wenn kein Netzwerk verfügbar und Cache vorhanden, verwende Cache auch wenn abgelaufen
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d("GameRepository", "[DEBUG] Kein Netzwerk, prüfe Offline-Cache für $gameId")
            Log.d("GameRepository", "Kein Netzwerk verfügbar")
            return if (cachedGame != null) {
                val game = cachedGame.toGame()
                Log.d("GameRepository", "Offline-Cache Screenshots: ${game.screenshots.size}")
                Resource.Success(game)
            } else {
                Resource.Error("Keine Internetverbindung und keine gecachten Daten verfügbar")
            }
        }

        return try {
            Log.d("GameRepository", "[DEBUG] Starte API-Call für Spieldetails $gameId")
            val resp = api.getGameDetail(gameId)
            
            if (resp.isSuccessful) {
                resp.body()?.let { gameDto ->
                    Log.d("GameRepository", "[DEBUG] API-Response erhalten: ${gameDto.name}")
                    Log.d(
                        "GameRepository",
                        "[DEBUG] API-Response Screenshots: ${gameDto.shortScreenshots?.size ?: 0}"
                    )
                    Log.d(
                        "GameRepository",
                        "API Website: '${gameDto.website}' (Typ: ${if (gameDto.website == null) "null" else "'${gameDto.website}'"})"
                    )
                    gameDto.shortScreenshots?.forEachIndexed { index, screenshot ->
                        Log.d("GameRepository", "API Screenshot $index: ${screenshot.image}")
                    }

                    // Lade Screenshots separat, da sie nicht automatisch mitgeliefert werden
                    val screenshots = try {
                        Log.d("GameRepository", "[DEBUG] Lade separate Screenshots für $gameId")
                        val screenshotsResp = api.getGameScreenshots(gameId, BuildConfig.API_KEY)
                        if (screenshotsResp.isSuccessful) {
                            val screenshotResponse = screenshotsResp.body()
                            screenshotResponse?.results?.map { it.image } ?: emptyList()
                        } else {
                            Log.w(
                                "GameRepository",
                                "Fehler beim Laden der Screenshots: ${screenshotsResp.code()}"
                            )
                            emptyList()
                        }
                    } catch (e: Exception) {
                        Log.e("GameRepository", "[ERROR] Exception beim Laden der Screenshots", e)
                        emptyList()
                    }

                    Log.d(
                        "GameRepository",
                        "[DEBUG] Separate Screenshots geladen: ${screenshots.size}"
                    )

                    val game = gameDto.toDomain().copy(screenshots = screenshots)
                    Log.d("GameRepository", "Konvertiert zu Domain: ${game.screenshots.size} Screenshots")
                    Log.d(
                        "GameRepository",
                        "Domain Website: '${game.website}' (Typ: ${if (game.website == null) "null" else "'${game.website}'"})"
                    )

                    // Cache das Spiel
                    // Screenshots nur überschreiben, wenn sie nicht schon im Cache vorhanden sind oder nicht leer sind
                    val cachedEntity = gameCacheDao.getGameById(game.id)
                    Log.d(
                        "GameRepository",
                        "[DEBUG] cachedEntity für Speicherung: ${cachedEntity != null}, Screenshots: ${cachedEntity?.toGame()?.screenshots?.size ?: 0}"
                    )
                    val cachedScreenshots = cachedEntity?.toGame()?.screenshots ?: emptyList()
                    val finalScreenshots =
                        if (cachedScreenshots.isNotEmpty()) cachedScreenshots else game.screenshots
                    val gameToCache = game.copy(screenshots = finalScreenshots)
                    gameCacheDao.insertGame(gameToCache.toCacheEntity())

                    Log.d(
                        "GameRepository",
                        "[DEBUG] Spiel wird gecacht: ${gameToCache.screenshots.size} Screenshots"
                    )

                    Resource.Success(game)
                } ?: Resource.Error("Leere Antwort von der API")
            } else {
                Log.e("GameRepository", "[ERROR] API-Fehler: ${resp.code()} für $gameId")
                // Fallback auf Cache wenn API fehlschlägt
                if (cachedGame != null) {
                    val game = cachedGame.toGame()
                    Log.d("GameRepository", "Fallback-Cache Screenshots: ${game.screenshots.size}")
                    Resource.Success(game)
                } else {
                    Resource.Error("Server Error ${resp.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Netzwerkfehler", e)
            // Fallback auf Cache bei Netzwerkfehler
            if (cachedGame != null) {
                val game = cachedGame.toGame()
                Log.d("GameRepository", "Error-Cache Screenshots: ${game.screenshots.size}")
                Resource.Success(game)
            } else {
                Resource.Error("Netzwerkfehler: ${e.localizedMessage}")
            }
        }
        Log.d("GameRepository", "[DEBUG] getGameDetail() fertig für $gameId")
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
            Resource.Error("Netzwerkfehler: ${e.localizedMessage}")
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
            Resource.Error("Netzwerkfehler: ${e.localizedMessage}")
        }
    }

    // PagingSource für die Suche mit Offline-Support
    private inner class GamePagingSource(
        private val query: String,
        private val platforms: String?,
        private val genres: String?,
        private val ordering: String?,
        private val rating: Float?
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
                        LoadResult.Error(Exception("Server Error ${resp.code()}"))
                    }
                }
            } catch (e: Exception) {
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
                    LoadResult.Error(e)
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
        rating: Float? = null
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
        Log.d("GameRepository", "Lösche gesamten Cache")
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
        try {
            val currentSize = gameCacheDao.getCacheSize()
            val oldestCacheTime = gameCacheDao.getOldestCacheTime()
            
            if (oldestCacheTime != null) {
                // Entferne Einträge die älter als 7 Tage sind
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                gameCacheDao.deleteExpiredGames(sevenDaysAgo)
                
                val newSize = gameCacheDao.getCacheSize()
                Log.d("GameRepository", "Cache optimiert: $currentSize -> $newSize Einträge")
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Fehler bei Cache-Optimierung", e)
        }
    }
    
    /**
     * Cache-Statistiken abrufen
     */
    suspend fun getCacheStats(): CacheStats {
        val totalSize = gameCacheDao.getCacheSize()
        val oldestTime = gameCacheDao.getOldestCacheTime()
        val isExpired = oldestTime?.let { !NetworkUtils.isCacheValid(it) } ?: true
        
        return CacheStats(
            totalEntries = totalSize,
            oldestEntryTime = oldestTime,
            isExpired = isExpired
        )
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
        prefs.edit()
            .putStringSet("last_known_game_ids", games.map { it.id.toString() }.toSet())
            .putStringSet("last_known_game_slugs", games.map { it.slug }.toSet())
            .apply()

        return newGames
    }
}

/**
 * Cache-Statistiken
 */
data class CacheStats(
    val totalEntries: Int,
    val oldestEntryTime: Long?,
    val isExpired: Boolean
)