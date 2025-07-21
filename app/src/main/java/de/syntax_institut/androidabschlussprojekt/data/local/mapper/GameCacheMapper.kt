package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Mapper für die Konvertierung zwischen Game und GameCacheEntity.
 * Bietet Funktionen zum Umwandeln von Domain-Modelle in Datenbank-Entities und umgekehrt.
 */
object GameCacheMapper {

    private val moshi = MoshiProvider.moshi

    private val listAdapter = moshi.adapter<List<String>>(List::class.java)
    private val movieListAdapter =
        moshi.adapter<List<Movie>>(
            Types.newParameterizedType(List::class.java, Movie::class.java)
        )

    /**
     * Wandelt ein Game-Domainmodell in eine GameCacheEntity um.
     * @receiver Game Das Domainmodell
     * @param searchQuery Optionaler Suchbegriff, zu dem das Spiel gecacht wurde
     * @param filterHash Optionaler Hashwert der Filtereinstellungen
     * @return Das entsprechende GameCacheEntity für die Datenbank
     */
    fun Game.toCacheEntity(
        searchQuery: String? = null,
        filterHash: String? = null,
    ): GameCacheEntity {
        return GameCacheEntity(
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
            genres = listAdapter.toJson(genres),
            platforms = listAdapter.toJson(platforms),
            developers = listAdapter.toJson(developers),
            publishers = listAdapter.toJson(publishers),
            tags = listAdapter.toJson(tags),
            screenshots = listAdapter.toJson(screenshots),
            stores = listAdapter.toJson(stores),
            playtime = playtime,
            movies = movieListAdapter.toJson(movies),
            searchQuery = searchQuery,
            filterHash = filterHash
        )
    }

    /**
     * Wandelt eine GameCacheEntity in ein Game-Domainmodell um.
     * @receiver GameCacheEntity Das Entity-Objekt aus der Datenbank
     * @return Das entsprechende Game-Domainmodell
     */
    fun GameCacheEntity.toGame(): Game {
        return Game(
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
            genres =
                try {
                    listAdapter.fromJson(genres) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            platforms =
                try {
                    listAdapter.fromJson(platforms) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            developers =
                try {
                    listAdapter.fromJson(developers) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            publishers =
                try {
                    listAdapter.fromJson(publishers) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            tags =
                try {
                    listAdapter.fromJson(tags) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            screenshots =
                try {
                    listAdapter.fromJson(screenshots) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            stores =
                try {
                    listAdapter.fromJson(stores) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                },
            playtime = playtime,
            movies =
                try {
                    movieListAdapter.fromJson(movies) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
        )
    }
}
