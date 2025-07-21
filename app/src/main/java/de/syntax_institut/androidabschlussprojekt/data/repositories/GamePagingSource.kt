package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toCacheEntity
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.GameCacheMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*

/**
 * PagingSource für die Spieleliste.
 * Lädt Spiele seitenweise aus der API und verwendet einen lokalen Cache als Fallback.
 *
 * @param api Instanz der RawgApi für Netzwerkanfragen
 * @param gameCacheDao DAO für den lokalen Spiele-Cache
 * @param context Anwendungskontext (für Netzwerkprüfung)
 * @param query Suchbegriff
 * @param platforms Plattform-Filter (optional)
 * @param genres Genre-Filter (optional)
 * @param ordering Sortierreihenfolge (optional)
 * @param rating Bewertungsfilter (optional)
 */
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
    /**
     * Lädt eine Seite von Spielen, verwendet Cache als Fallback.
     *
     * @param params Paging-Parameter (z.B. Seitengröße, Schlüssel)
     * @return LoadResult mit einer Seite von Spielen oder einem Fehler
     */
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
                return LoadResult.Error(Exception(context.getString(R.string.error_games_no_network_cache)))
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
                    LoadResult.Error(Exception(context.getString(R.string.error_server)))
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
                        context.getString(R.string.error_server) + ": " + (e.localizedMessage
                            ?: context.getString(R.string.error_unknown))
                    )
                )
            }
        }
    }

    /**
     * Gibt den Schlüssel für das Refresh der PagingSource zurück.
     *
     * @param state Aktueller PagingState
     * @return Immer 1 (erste Seite)
     */
    override fun getRefreshKey(state: PagingState<Int, Game>): Int? = 1
}