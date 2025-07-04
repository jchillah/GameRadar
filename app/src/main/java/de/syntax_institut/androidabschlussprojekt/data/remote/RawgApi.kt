package de.syntax_institut.androidabschlussprojekt.data.remote

import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import retrofit2.*
import retrofit2.http.*

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

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String = BuildConfig.API_KEY,
    ): Response<ScreenshotResponse>

    @GET("platforms")
    suspend fun getPlatforms(
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Response<PlatformResponse>

    @GET("genres")
    suspend fun getGenres(
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Response<GenreResponse>
}