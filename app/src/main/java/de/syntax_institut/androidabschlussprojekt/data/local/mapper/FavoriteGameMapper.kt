package de.syntax_institut.androidabschlussprojekt.data.local.mapper

import android.util.*
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

    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
    )
    private val movieListAdapter = moshi.adapter<List<Movie>>(
        Types.newParameterizedType(
            List::class.java,
            Movie::class.java
        )
    )
    
    /**
     * Game zu FavoriteGameEntity konvertieren.
     */
    fun Game.toFavoriteEntity(): FavoriteGameEntity {
        Log.d("FavoriteGameMapper", "[DEBUG] Konvertiere Game zu Entity: ${title}")
        Log.d("FavoriteGameMapper", "[DEBUG] Game Screenshots: ${screenshots.size}")
        Log.d("FavoriteGameMapper", "[DEBUG] Game Movies: ${movies.size}")

        val screenshotsJson = try {
            val json = stringListAdapter.toJson(screenshots)
            Log.d("FavoriteGameMapper", "[DEBUG] Screenshots JSON: $json")
            json
        } catch (e: Exception) {
            Log.e("FavoriteGameMapper", "Fehler beim Serialisieren von screenshots: ${e.message}")
            "[]"
        }

        val moviesJson = try {
            val json = movieListAdapter.toJson(movies)
            Log.d("FavoriteGameMapper", "[DEBUG] Movies JSON: $json")
            json
        } catch (e: Exception) {
            Log.e("FavoriteGameMapper", "Fehler beim Serialisieren von movies: ${e.message}")
            "[]"
        }
        
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
            genres = stringListAdapter.toJson(genres),
            platforms = stringListAdapter.toJson(platforms),
            developers = stringListAdapter.toJson(developers),
            publishers = stringListAdapter.toJson(publishers),
            tags = stringListAdapter.toJson(tags),
            screenshots = screenshotsJson,
            stores = stringListAdapter.toJson(stores),
            playtime = playtime,
            movies = moviesJson
        )
    }
    
    /**
     * FavoriteGameEntity zu Game konvertieren.
     */
    fun FavoriteGameEntity.toGame(): Game {
        Log.d("FavoriteGameMapper", "[DEBUG] Konvertiere Entity zu Game: ${title}")
        Log.d("FavoriteGameMapper", "[DEBUG] Entity Screenshots JSON: $screenshots")
        Log.d("FavoriteGameMapper", "[DEBUG] Entity Movies JSON: $movies")

        val parsedScreenshots = try {
            val result = stringListAdapter.fromJson(screenshots) ?: emptyList()
            Log.d("FavoriteGameMapper", "[DEBUG] Parsed Screenshots: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e("FavoriteGameMapper", "Fehler beim Parsen von screenshots: ${e.message}")
            Log.e("FavoriteGameMapper", "Screenshots JSON: $screenshots")
            emptyList()
        }

        val parsedMovies = try {
            val result = movieListAdapter.fromJson(movies) ?: emptyList()
            Log.d("FavoriteGameMapper", "[DEBUG] Parsed Movies: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e("FavoriteGameMapper", "Fehler beim Parsen von movies: ${e.message}")
            Log.e("FavoriteGameMapper", "Movies JSON: $movies")
            emptyList()
        }

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
                stringListAdapter.fromJson(genres) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von genres: ${e.message}")
                emptyList()
            },
            platforms = try {
                stringListAdapter.fromJson(platforms) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von platforms: ${e.message}")
                emptyList()
            },
            developers = try {
                stringListAdapter.fromJson(developers) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von developers: ${e.message}")
                emptyList()
            },
            publishers = try {
                stringListAdapter.fromJson(publishers) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von publishers: ${e.message}")
                emptyList()
            },
            tags = try {
                stringListAdapter.fromJson(tags) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von tags: ${e.message}")
                emptyList()
            },
            screenshots = parsedScreenshots,
            stores = try {
                stringListAdapter.fromJson(stores) ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoriteGameMapper", "Fehler beim Parsen von stores: ${e.message}")
                emptyList()
            },
            playtime = playtime,
            movies = parsedMovies
        )
    }
} 