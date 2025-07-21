package de.syntax_institut.androidabschlussprojekt.data.remote

import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import retrofit2.*
import retrofit2.http.*

/**
 * Retrofit-API-Interface für die RAWG-API.
 * Definiert alle Endpunkte zur Spiele-, Detail-, Screenshot-, Plattform-, Genre- und Trailer-Abfrage.
 */
interface RawgApi {
    /**
     * Sucht Spiele anhand verschiedener Filter und Suchparameter.
     *
     * @param query Suchbegriff (optional)
     * @param platforms Plattform-IDs als Komma-separierte Liste (optional)
     * @param genres Genre-IDs als Komma-separierte Liste (optional)
     * @param ordering Sortierreihenfolge (optional)
     * @param page Seitenzahl für die Paginierung (optional)
     * @param pageSize Anzahl der Ergebnisse pro Seite (optional)
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit einer Liste von Spielen
     */
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

    /**
     * Ruft Spieldetails zu einer bestimmten ID ab.
     *
     * @param id Die ID des Spiels
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit den Spieldetails
     */
    @GET(Constants.ENDPOINT_GAME_DETAIL)
    suspend fun getGameDetail(
        @Path(Constants.ENDPOINT_ID) id: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GameDto>

    /**
     * Ruft Screenshots zu einem bestimmten Spiel ab.
     *
     * @param gameId Die ID des Spiels
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit einer Liste von Screenshots
     */
    @GET(Constants.ENDPOINT_GAME_SCREENSHOTS)
    suspend fun getGameScreenshots(
        @Path(Constants.ENDPOINT_ID) gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<ScreenshotResponse>

    /**
     * Ruft alle verfügbaren Plattformen ab.
     *
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit einer Liste von Plattformen
     */
    @GET(Constants.ENDPOINT_PLATFORMS)
    suspend fun getPlatforms(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<PlatformResponse>

    /**
     * Ruft alle verfügbaren Genres ab.
     *
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit einer Liste von Genres
     */
    @GET(Constants.ENDPOINT_GENRES)
    suspend fun getGenres(
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<GenreResponse>

    /**
     * Ruft Trailer/Videos zu einem bestimmten Spiel ab.
     *
     * @param gameId Die ID des Spiels
     * @param apiKey API-Schlüssel (Standard: BuildConfig.API_KEY)
     * @return HTTP-Response mit einer Liste von Trailern/Videos
     */
    @GET(Constants.ENDPOINT_GAME_MOVIES)
    suspend fun getGameMovies(
        @Path(Constants.ENDPOINT_ID) gameId: Int,
        @Query(Constants.API_KEY_PARAM) apiKey: String = BuildConfig.API_KEY,
    ): Response<MovieResponse>
}