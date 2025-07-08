package de.syntax_institut.androidabschlussprojekt.data.remote

import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import retrofit2.*
import retrofit2.http.*

interface RawgApi {
    @GET("games")
    suspend fun searchGames(
        @Query(Constants.SEARCH_PARAM) query: String? = null,
        @Query(Constants.PLATFORMS_PARAM) platforms: String? = null,
        @Query(Constants.GENRES_PARAM) genres: String? = null,
        @Query(Constants.ORDERING_PARAM) ordering: String? = null,
        @Query(Constants.PAGE_PARAM) page: Int? = null,
        @Query(Constants.PAGE_SIZE_PARAM) pageSize: Int? = null,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GamesResponse>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GameDto>

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<ScreenshotResponse>

    @GET("platforms")
    suspend fun getPlatforms(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<PlatformResponse>

    @GET("genres")
    suspend fun getGenres(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GenreResponse>

    @GET("games/{id}/movies")
    suspend fun getGameMovies(
        @Path("id") gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<MovieResponse>
}