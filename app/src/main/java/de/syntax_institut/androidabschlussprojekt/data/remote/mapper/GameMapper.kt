package de.syntax_institut.androidabschlussprojekt.data.remote.mapper

import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GameDto

fun GameDto.toDomain(): Game = Game(
    id = id,
    title = name,
    releaseDate = released,
    imageUrl = backgroundImage,
    rating = rating,
    description = description,
    metacritic = metacritic,
    website = website,
    esrbRating = esrbRating?.name,
    genres = genres?.map { it.name } ?: emptyList(),
    platforms = platforms?.map { it.platform.name } ?: emptyList(),
    developers = developers?.map { it.name } ?: emptyList(),
    publishers = publishers?.map { it.name } ?: emptyList(),
    tags = tags?.map { it.name } ?: emptyList(),
    screenshots = shortScreenshots?.map { it.image } ?: emptyList(),
    stores = stores?.map { it.store.name } ?: emptyList(),
    playtime = playtime
)