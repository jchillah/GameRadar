package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import android.net.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase für den Import der Wunschliste von einer URI.
 *
 * Kapselt die Logik für das Importieren der Wunschliste aus dem Repository.
 */
class ImportWishlistFromUriUseCase(private val repository: WishlistRepository) {
    /**
     * Importiert die Wunschliste von einer angegebenen URI.
     * @param context Der Anwendungskontext
     * @param uri Die Quell-URI für den Import
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, uri: Uri): Resource<Unit> {
        return repository.importWishlistFromUri(context, uri)
    }
}
