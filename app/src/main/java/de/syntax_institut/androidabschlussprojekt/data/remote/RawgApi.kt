package de.syntax_institut.androidabschlussprojekt.data.remote

import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import retrofit2.*
import retrofit2.http.*

interface RawgApi {
    @GET(Constants.ENDPOINT_GAMES)
    suspend fun searchGames(
        @Query(Constants.SEARCH_PARAM) query: String? = null,
        @Query(Constants.PLATFORMS_PARAM) platforms: String? = null,
        @Query(Constants.GENRES_PARAM) genres: String? = null,
        @Query(Constants.ORDERING_PARAM) ordering: String? = null,
        @Query(Constants.PAGE_PARAM) page: Int? = null,
        @Query(Constants.PAGE_SIZE_PARAM) pageSize: Int? = null,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GamesResponse>

    @GET(Constants.ENDPOINT_GAME_DETAIL)
    suspend fun getGameDetail(
        @Path(Constants.ENDPOINT_ID) id: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GameDto>

    @GET(Constants.ENDPOINT_GAME_SCREENSHOTS)
    suspend fun getGameScreenshots(
        @Path(Constants.ENDPOINT_ID) gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<ScreenshotResponse>

    @GET(Constants.ENDPOINT_PLATFORMS)
    suspend fun getPlatforms(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<PlatformResponse>

    @GET(Constants.ENDPOINT_GENRES)
    suspend fun getGenres(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GenreResponse>

    @GET(Constants.ENDPOINT_GAME_MOVIES)
    suspend fun getGameMovies(
        @Path(Constants.ENDPOINT_ID) gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<MovieResponse>
}