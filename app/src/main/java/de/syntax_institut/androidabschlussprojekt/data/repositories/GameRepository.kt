package de.syntax_institut.androidabschlussprojekt.data.repositories

import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import de.syntax_institut.androidabschlussprojekt.data.remote.mapper.toDomain
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.utils.Resource
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val api: RawgApi
) {
    suspend fun searchGames(query: String): Resource<List<Game>> = try {
        val resp = api.searchGames(query = query)
        if (resp.isSuccessful) {
            Resource.Success(resp.body()?.results?.map { it.toDomain() } ?: emptyList())
        } else {
            Resource.Error("Server Error ${resp.code()}")
        }
    } catch (e: Exception) {
        Resource.Error("Network Error: ${e.localizedMessage}")
    }

    suspend fun getPlatforms(): Resource<List<String>> = try {
 val resp = api.getPlatforms()
        if (resp.isSuccessful) {
 Resource.Success(resp.body()?.results?.mapNotNull { it.name } ?: emptyList())
        } else {
 Resource.Error("Server Error ${resp.code()}")
        }
    } catch (e: Exception) {
 Resource.Error("Network Error: ${e.localizedMessage}")
    }

    suspend fun getGenres(): Resource<List<String>> = try {
 val resp = api.getGenres()
        if (resp.isSuccessful) {
 Resource.Success(resp.body()?.results?.mapNotNull { it.name } ?: emptyList())
        } else {
 Resource.Error("Server Error ${resp.code()}")
        }
    } catch (e: Exception) {
 Resource.Error("Network Error: ${e.localizedMessage}")
    }

    suspend fun getGameDetail(id: Int): Resource<Game> = try {
        val resp = api.getGameDetail(gameId = id)
        if (resp.isSuccessful) {
            resp.body()?.toDomain()?.let { Resource.Success(it) }
                ?: Resource.Error("No data")
        } else {
            Resource.Error("Server Error ${resp.code()}")
        }
    } catch (e: Exception) {
        Resource.Error("Network Error: ${e.localizedMessage}")
    }

    // Smoke‑Test:
    suspend fun smokeTest(): Boolean {
        // Einfach mal den Endpunkt ohne Query aufrufen
        val response = api.searchGames(query = "")
        return response.isSuccessful
    }
}