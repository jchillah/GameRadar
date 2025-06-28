package de.syntax_institut.androidabschlussprojekt.data.repositories

import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.toDomain
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import javax.inject.Inject
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GameRepository @Inject constructor(
    private val api: RawgApi
) {

    suspend fun getGameDetail(id: Int): Resource<Game> = try {
        val resp = api.getGameDetail(gameId = id)
        if (resp.isSuccessful) {
            resp.body()?.toDomain()?.let { Resource.Success(it) }
                ?: Resource.Error("No data")
        } else {
            Resource.Error("Server Error ${resp.code()}")
        }
    } catch (e: Exception) {
        Resource.Error("Network Error: ${e.localizedMessage}")
    }

    // PagingSource für die Suche
    private inner class GamePagingSource(
        private val query: String,
        private val platforms: String?,
        private val genres: String?,
        private val ordering: String?,
        private val rating: Float?
    ) : PagingSource<Int, Game>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
            val page = params.key ?: 1
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
                    val nextPage = if (body?.next != null) page + 1 else null
                    LoadResult.Page(
                        data = filteredGames,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextPage
                    )
                } else {
                    LoadResult.Error(Exception("Server Error ${resp.code()}"))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
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
}