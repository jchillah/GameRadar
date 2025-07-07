package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * Mapper für die Konvertierung zwischen Game und GameCacheEntity.
 */
object GameCacheMapper {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val listAdapter = moshi.adapter<List<String>>(List::class.java)
    
    /**
     * Game zu GameCacheEntity konvertieren.
     */
    fun Game.toCacheEntity(
        searchQuery: String? = null,
        filterHash: String? = null
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
            searchQuery = searchQuery,
            filterHash = filterHash
        )
    }
    
    /**
     * GameCacheEntity zu Game konvertieren.
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
            genres = try {
                listAdapter.fromJson(genres) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            platforms = try {
                listAdapter.fromJson(platforms) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            developers = try {
                listAdapter.fromJson(developers) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            publishers = try {
                listAdapter.fromJson(publishers) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            tags = try {
                listAdapter.fromJson(tags) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            screenshots = try {
                listAdapter.fromJson(screenshots) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            stores = try {
                listAdapter.fromJson(stores) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            },
            playtime = playtime
        )
    }
} 