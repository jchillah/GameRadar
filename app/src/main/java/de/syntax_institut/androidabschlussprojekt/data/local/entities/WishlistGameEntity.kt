package de.syntax_institut.androidabschlussprojekt.data.local.entities

import androidx.room.*

@Entity(tableName = "wishlist_games")
data class WishlistGameEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val title: String,
    val releaseDate: String?,
    val imageUrl: String?,
    val rating: Float,
    val description: String?,
    val metacritic: Int?,
    val website: String?,
    val esrbRating: String?,
    val screenshots: String, // JSON-String
    val movies: String, // JSON-String
)
