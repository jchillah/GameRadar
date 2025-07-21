package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Mapper für die Konvertierung zwischen WishlistGameEntity und Game.
 * Bietet Funktionen zum Umwandeln von Datenbank-Entities in Domain-Modelle und umgekehrt.
 */
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

    /**
     * Wandelt eine WishlistGameEntity in ein Game-Domainmodell um.
     * @receiver WishlistGameEntity Das Entity-Objekt aus der Datenbank
     * @return Das entsprechende Game-Domainmodell
     */
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

    /**
     * Wandelt ein Game-Domainmodell in eine WishlistGameEntity um.
     * @receiver Game Das Domainmodell
     * @return Das entsprechende WishlistGameEntity für die Datenbank
     */
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
