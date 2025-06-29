package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.Context
import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.toDomain
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.local.dao.GameCacheDao
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import de.syntax_institut.androidabschlussprojekt.utils.NetworkUtils
import de.syntax_institut.androidabschlussprojekt.domain.models.Platform
import de.syntax_institut.androidabschlussprojekt.domain.models.Genre
import javax.inject.Inject
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import android.util.Log

class GameRepository @Inject constructor(
    private val api: RawgApi,
    private val gameCacheDao: GameCacheDao,
    private val context: Context
) {

    suspend fun getGameDetail(id: Int): Resource<Game> {
        Log.d("GameRepository", "Lade Spieldetails für ID: $id")
        
        // Zuerst versuchen aus Cache zu laden
        val cachedGame = gameCacheDao.getGameById(id)
        if (cachedGame != null && NetworkUtils.isCacheValid(cachedGame.cachedAt)) {
            Log.d("GameRepository", "Verwende gecachte Daten")
            val game = cachedGame.toGame()
            Log.d("GameRepository", "Gecachte Screenshots: ${game.screenshots.size}")
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
        
        // Versuche API-Call
        Log.d("GameRepository", "API-Call für Spieldetails")
        return try {
            val resp = api.getGameDetail(gameId = id)
            if (resp.isSuccessful) {
                resp.body()?.let { gameDto ->
                    Log.d("GameRepository", "API-Antwort erhalten: ${gameDto.name}")
                    Log.d("GameRepository", "API Screenshots: ${gameDto.shortScreenshots?.size ?: 0}")
                    gameDto.shortScreenshots?.forEachIndexed { index, screenshot ->
                        Log.d("GameRepository", "API Screenshot $index: ${screenshot.image}")
                    }
                    
                    val game = gameDto.toDomain()
                    Log.d("GameRepository", "Konvertiert zu Domain: ${game.screenshots.size} Screenshots")
                    
                    // Speichere in Cache
                    gameCacheDao.insertGame(game.toCacheEntity())
                    Resource.Success(game)
                } ?: Resource.Error("Keine Daten erhalten")
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
        gameCacheDao.clearAllGames()
    }
    
    suspend fun clearOldCache() {
        val maxAge = System.currentTimeMillis() - NetworkUtils.CACHE_INVALIDATION_TIME
        gameCacheDao.clearOldCache(maxAge)
    }
    
    suspend fun getCacheSize(): Int {
        return gameCacheDao.getCacheSize()
    }
    
    /**
     * Prüft ob Daten für Query im Cache verfügbar sind
     */
    suspend fun isQueryCached(query: String): Boolean {
        return gameCacheDao.isQueryCached(query)
    }
}