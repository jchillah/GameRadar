package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity für gecachte Spiele.
 * Speichert Spieldaten lokal für Offline-Zugriff mit Cache-Invalidierung.
 */
@Entity(tableName = "game_cache")
data class GameCacheEntity(
    @PrimaryKey
    val id: Int,
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
    val cachedAt: Long = System.currentTimeMillis(),
    val searchQuery: String? = null,
    val filterHash: String? = null
) 