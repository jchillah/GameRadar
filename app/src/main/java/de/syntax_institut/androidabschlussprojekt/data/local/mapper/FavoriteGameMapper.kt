package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/** Mapper für die Konvertierung zwischen Game und FavoriteGameEntity. */
object FavoriteGameMapper {
    private val moshi = MoshiProvider.moshi

    private val stringListAdapter =
        moshi.adapter<List<String>>(
            Types.newParameterizedType(List::class.java, String::class.java)
        )
    private val movieListAdapter =
        moshi.adapter<List<Movie>>(
            Types.newParameterizedType(List::class.java, Movie::class.java)
        )

    // Hilfsfunktionen für sicheres Parsen
    private fun parseStringList(json: String): List<String> =
        try {
            stringListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            AppLogger.e(
                "FavoriteGameMapper",
                "${Constants.ERROR} beim Parsen der String-Liste: ${e.localizedMessage}",
                e
            )
            emptyList()
        }

    private fun parseMovieList(json: String): List<Movie> =
        try {
            movieListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            AppLogger.e(
                "FavoriteGameMapper",
                "${Constants.ERROR} beim Parsen der Movie-Liste: ${e.localizedMessage}",
                e
            )
            emptyList()
        }

    private fun toJsonStringList(list: List<String>): String =
        try {
            stringListAdapter.toJson(list)
        } catch (e: Exception) {
            AppLogger.e(
                "FavoriteGameMapper",
                "${Constants.ERROR} beim Serialisieren der String-Liste: ${e.localizedMessage}",
                e
            )
            Constants.EMPTY_JSON_ARRAY
        }

    private fun toJsonMovieList(list: List<Movie>): String =
        try {
            movieListAdapter.toJson(list)
        } catch (e: Exception) {
            AppLogger.e(
                "FavoriteGameMapper",
                "${Constants.ERROR} beim Serialisieren der Movie-Liste: ${e.localizedMessage}",
                e
            )
            Constants.EMPTY_JSON_ARRAY
        }

    /** Game zu FavoriteGameEntity konvertieren. */
    fun Game.toFavoriteEntity(): FavoriteGameEntity =
        FavoriteGameEntity(
            id = id,
            slug = slug,
            title = title,
            releaseDate = releaseDate,
            imageUrl = imageUrl,
            rating = rating,
            description = description,
            metacritic = metacritic,
            website = website,
            esrbRating = esrbRating,
            genres = toJsonStringList(genres),
            platforms = toJsonStringList(platforms),
            developers = toJsonStringList(developers),
            publishers = toJsonStringList(publishers),
            tags = toJsonStringList(tags),
            screenshots = toJsonStringList(screenshots),
            stores = toJsonStringList(stores),
            playtime = playtime,
            movies = toJsonMovieList(movies)
        )

    /** FavoriteGameEntity zu Game konvertieren. */
    fun FavoriteGameEntity.toGame(): Game =
        Game(
            id = id,
            slug = slug,
            title = title,
            releaseDate = releaseDate,
            imageUrl = imageUrl,
            rating = rating,
            description = description,
            metacritic = metacritic,
            website = website,
            esrbRating = esrbRating,
            genres = parseStringList(genres),
            platforms = parseStringList(platforms),
            developers = parseStringList(developers),
            publishers = parseStringList(publishers),
            tags = parseStringList(tags),
            screenshots = parseStringList(screenshots),
            stores = parseStringList(stores),
            playtime = playtime,
            movies = parseMovieList(movies)
        )
}
