package de.syntax_institut.androidabschlussprojekt.domain.usecase

import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import kotlinx.coroutines.flow.*

/**
 * UseCase zum Abrufen aller Spiele auf der Wunschliste.
 *
 * Kapselt die Logik für das Laden der Wunschlistenspiele aus dem Repository.
 */
class GetAllWishlistGamesUseCase(private val repository: WishlistRepository) {
    /**
     * Gibt einen Flow mit allen Spielen auf der Wunschliste zurück.
     * @return Flow<List<Game>> mit allen Wunschlistenspielen
     */
    operator fun invoke(): Flow<List<Game>> = repository.getAllWishlistGames()
}
