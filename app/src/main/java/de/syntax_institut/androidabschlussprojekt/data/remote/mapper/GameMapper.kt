package de.syntax_institut.androidabschlussprojekt.data.remote.mapper

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*

fun GameDto.toDomain(): Game =
    Game(
        id = id,
        slug = slug,
        title = name, // <- name bleibt hier, da RAWG-API das Feld so liefert
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

fun MovieDto.toDomain(): Movie =
    Movie(id = id, name = name, preview = preview, url480 = data.low, urlMax = data.max)
