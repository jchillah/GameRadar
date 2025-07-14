package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

object WishlistGameMapper {
    private val moshi = MoshiProvider.moshi
    private val stringListAdapter =
        moshi.adapter<List<String>>(
            Types.newParameterizedType(List::class.java, String::class.java)
        )
    private val movieListAdapter =
        moshi.adapter<List<Movie>>(
            Types.newParameterizedType(List::class.java, Movie::class.java)
        )

    fun WishlistGameEntity.toGame(): Game =
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
            screenshots = stringListAdapter.fromJson(screenshots) ?: emptyList(),
            movies = movieListAdapter.fromJson(movies) ?: emptyList()
        )

    fun Game.toWishlistEntity(): WishlistGameEntity =
        WishlistGameEntity(
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
            screenshots = stringListAdapter.toJson(screenshots),
            movies = movieListAdapter.toJson(movies)
        )
}
