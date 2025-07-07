package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * Mapper für die Konvertierung zwischen Game und FavoriteGameEntity.
 */
object FavoriteGameMapper {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val listAdapter = moshi.adapter<List<String>>(List::class.java)
    
    /**
     * Game zu FavoriteGameEntity konvertieren.
     */
    fun Game.toFavoriteEntity(): FavoriteGameEntity {
        return FavoriteGameEntity(
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
            playtime = playtime
        )
    }
    
    /**
     * FavoriteGameEntity zu Game konvertieren.
     */
    fun FavoriteGameEntity.toGame(): Game {
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
            } catch (e: Exception) {
                emptyList()
            },
            platforms = try {
                listAdapter.fromJson(platforms) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            developers = try {
                listAdapter.fromJson(developers) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            publishers = try {
                listAdapter.fromJson(publishers) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            tags = try {
                listAdapter.fromJson(tags) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            screenshots = try {
                listAdapter.fromJson(screenshots) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            stores = try {
                listAdapter.fromJson(stores) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            playtime = playtime
        )
    }
} 