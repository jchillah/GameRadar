package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*

class GamePagingSource(
    private val api: RawgApi,
    private val gameCacheDao: GameCacheDao,
    private val context: Context,
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
                return LoadResult.Error(Exception("Fehler beim Laden der Spiele: Kein Netzwerk und kein Cache verfügbar"))
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
                // Speichere ALLE geladenen Spiele in den Cache (nicht nur Seite 1)
                val cacheEntities = allGames.map { game ->
                    game.toCacheEntity(query, filterHash)
                }
                AppLogger.d(
                    "dgc",
                    "[PAGE $page] Versuche ${cacheEntities.size} Spiele in den Cache zu schreiben. Query='$query', FilterHash='$filterHash'"
                )
                try {
                    gameCacheDao.insertGames(cacheEntities)
                    AppLogger.d(
                        "dgc",
                        "[PAGE $page] Insert erfolgreich für ${cacheEntities.size} Spiele. Query='$query', FilterHash='$filterHash'"
                    )
                } catch (e: Exception) {
                    AppLogger.e(
                        "dgc",
                        "[PAGE $page] Fehler beim Insert in den Cache: ${e.localizedMessage}",
                        e
                    )
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
                    LoadResult.Error(Exception("Serverfehler"))
                }
            }
        } catch (e: Exception) {
            AppLogger.e("GameRepository", "${Constants.ERROR} beim API-Call in PagingSource", e)
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
                        // Ursprünglich: ErrorHandler.handleException(e, "Serverfehler")
                        // Jetzt: Nur einfacher Fehlertext, keine Compose-Funktion!
                        "Serverfehler: " + (e.localizedMessage ?: "Unbekannter Fehler")
                    )
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? = 1
}