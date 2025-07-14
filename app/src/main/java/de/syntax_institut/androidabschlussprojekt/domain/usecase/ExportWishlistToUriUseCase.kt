package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import android.net.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase für den Export der Wunschliste zu einer URI. Folgt Clean Code Best Practices: Single
 * Responsibility, DRY, KISS.
 */
class ExportWishlistToUriUseCase(private val repository: WishlistRepository) {

    suspend operator fun invoke(context: Context, uri: Uri): Resource<Unit> {
        return repository.exportWishlistToUri(context, uri)
    }
}
