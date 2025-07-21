package de.syntax_institut.androidabschlussprojekt.domain.usecase

import android.content.*
import android.net.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * UseCase für den Export der Wunschliste zu einer URI.
 *
 * Kapselt die Logik für das Exportieren der Wunschliste aus dem Repository.
 */
class ExportWishlistToUriUseCase(private val repository: WishlistRepository) {
    /**
     * Exportiert die Wunschliste zu einer angegebenen URI.
     * @param context Der Anwendungskontext
     * @param uri Die Ziel-URI für den Export
     * @return Resource<Unit> mit Erfolg oder Fehler
     */
    suspend operator fun invoke(context: Context, uri: Uri): Resource<Unit> {
        return repository.exportWishlistToUri(context, uri)
    }
}
