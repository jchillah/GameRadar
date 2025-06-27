package de.syntax_institut.androidabschlussprojekt.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import de.syntax_institut.androidabschlussprojekt.BuildConfig
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GameDto
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GamesResponse

interface RawgApi {
    @GET("games")
    suspend fun searchGames(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("search") query: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("platforms") platforms: String? = null,
        @Query("genres") genres: String? = null,
        @Query("ordering") ordering: String? = null
    ): Response<GamesResponse>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Response<GameDto>
}