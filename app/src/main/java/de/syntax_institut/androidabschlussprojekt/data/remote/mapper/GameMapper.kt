package de.syntax_institut.androidabschlussprojekt.data.remote.mapper

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*

/**
 * Mapper-Funktionen zur Umwandlung von DTOs in Domain-Modelle. Bietet Extension-Funktionen für
 * GameDto und MovieDto.
 */

/**
 * Wandelt ein GameDto in ein Game-Domainmodell um.
 *
 * @receiver GameDto Das DTO-Objekt aus der API
 * @return Das entsprechende Game-Domainmodell
 */
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
        // movies wird in der Repository-Logik durch copy() gesetzt
    )

/**
 * Wandelt ein MovieDto in ein Movie-Domainmodell um.
 *
 * @receiver MovieDto Das DTO-Objekt aus der API
 * @return Das entsprechende Movie-Domainmodell
 */
fun MovieDto.toDomain(): Movie =
    Movie(id = id, name = name, preview = preview, url480 = data.low, urlMax = data.max)
