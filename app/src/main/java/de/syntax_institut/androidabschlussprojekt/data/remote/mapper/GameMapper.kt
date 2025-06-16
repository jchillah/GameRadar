package de.syntax_institut.androidabschlussprojekt.data.remote.mapper

import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GameDto

fun GameDto.toDomain(): Game = Game(
    id = id,
    title = name,
    releaseDate = released,
    imageUrl = backgroundImage,
    rating = rating,
    description = description
)