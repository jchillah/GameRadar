package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import android.util.*
import androidx.paging.*
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
    private val context: Context
) {

    suspend fun getGameDetail(gameId: Int): Resource<Game> {
        Log.d("GameRepository", "Lade Spieldetails für ID: $gameId")

        // Prüfe zuerst den Cache
        val cachedGame = gameCacheDao.getGameById(gameId)
        if (cachedGame != null && NetworkUtils.isCacheValid(cachedGame.cachedAt)) {
            Log.d("GameRepository", "Verwende gecachte Daten")
            val game = cachedGame.toGame()
            Log.d("GameRepository", "Gecachte Screenshots: ${game.screenshots.size}")
            Log.d("GameRepository", "Gecachte Website: '${game.website}'")
            return Resource.Success(game)
        }

        // Wenn kein Netzwerk verfügbar und Cache vorhanden, verwende Cache auch wenn abgelaufen
        if (!NetworkUtils.isNetworkAvailable(context)) {
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
            Log.d("GameRepository", "API-Call für Spieldetails")
            val resp = api.getGameDetail(gameId)
            
            if (resp.isSuccessful) {
                resp.body()?.let { gameDto ->
                    Log.d("GameRepository", "API-Antwort erhalten: ${gameDto.name}")
                    Log.d("GameRepository", "API Screenshots: ${gameDto.shortScreenshots?.size ?: 0}")
                    Log.d("GameRepository", "API Website: '${gameDto.website}'")
                    gameDto.shortScreenshots?.forEachIndexed { index, screenshot ->
                        Log.d("GameRepository", "API Screenshot $index: ${screenshot.image}")
                    }

                    // Lade Screenshots separat, da sie nicht automatisch mitgeliefert werden
                    val screenshots = try {
                        Log.d("GameRepository", "Lade Screenshots separat für ID: $gameId")
                        val screenshotsResp = api.getGameScreenshots(gameId)
                        if (screenshotsResp.isSuccessful) {
                            screenshotsResp.body()?.map { it.image } ?: emptyList()
                        } else {
                            Log.w(
                                "GameRepository",
                                "Fehler beim Laden der Screenshots: ${screenshotsResp.code()}"
                            )
                            emptyList()
                        }
                    } catch (e: Exception) {
                        Log.w(
                            "GameRepository",
                            "Exception beim Laden der Screenshots: ${e.message}"
                        )
                        emptyList()
                    }

                    Log.d("GameRepository", "Separate Screenshots geladen: ${screenshots.size}")

                    val game = gameDto.toDomain().copy(screenshots = screenshots)
                    Log.d("GameRepository", "Konvertiert zu Domain: ${game.screenshots.size} Screenshots")
                    Log.d("GameRepository", "Domain Website: '${game.website}'")

                    // Cache das Spiel
                    gameCacheDao.insertGame(game.toCacheEntity())

                    Resource.Success(game)
                } ?: Resource.Error("Leere Antwort von der API")
            } else {
                Log.e("GameRepository", "API-Fehler: ${resp.code()}")
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
}