package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.*

/**
 * Room Entity für gecachte Spieldetails (Detailansicht).
 * Speichert vollständige Spieldaten für die Detailansicht, unabhängig vom Paging-Cache.
 */
@Entity(tableName = "game_detail_cache")
data class GameDetailCacheEntity(
    @PrimaryKey
    val id: Int,
    val slug: String = "",
    val title: String,
    val releaseDate: String?,
    val imageUrl: String?,
    val rating: Float,
    val description: String?,
    val metacritic: Int?,
    val website: String?,
    val esrbRating: String?,
    val genres: String,
    val platforms: String,
    val developers: String,
    val publishers: String,
    val tags: String,
    val screenshots: String,
    val stores: String,
    val playtime: Int?,
    val movies: String,
    val detailCachedAt: Long = System.currentTimeMillis(),
) 