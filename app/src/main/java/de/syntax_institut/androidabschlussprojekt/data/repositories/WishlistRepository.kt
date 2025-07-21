package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import android.net.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.WishlistGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.WishlistGameMapper.toWishlistEntity
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import java.io.*

/**
 * Repository zur Verwaltung der Wunschliste. Kapselt alle Datenbankoperationen und
 * Import/Export-Funktionen für die Wunschliste.
 *
 * @constructor Initialisiert das Repository mit dem zugehörigen DAO
 * @param wishlistGameDao DAO für die Wunschlisten-Tabelle
 */
class WishlistRepository(private val wishlistGameDao: WishlistGameDao) {
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private val type = Types.newParameterizedType(List::class.java, Game::class.java)
        private val adapter = moshi.adapter<List<Game>>(type)

    /**
     * Gibt alle Spiele in der Wunschliste als Flow zurück.
     * @return Flow mit einer Liste von Game-Objekten
     */
        fun getAllWishlistGames(): Flow<List<Game>> =
                wishlistGameDao.getAllWishlistGames().map { it.map { entity -> entity.toGame() } }

    /**
     * Prüft, ob ein Spiel in der Wunschliste ist.
     * @param gameId Die ID des Spiels
     * @return true, wenn das Spiel in der Wunschliste ist
     */
        suspend fun isInWishlist(gameId: Int): Boolean = wishlistGameDao.isInWishlist(gameId)

    /**
     * Gibt ein Spiel aus der Wunschliste anhand der ID zurück.
     * @param gameId Die ID des Spiels
     * @return Das gefundene Game-Objekt oder null
     */
        suspend fun getWishlistGameById(gameId: Int): Game? =
                wishlistGameDao.getWishlistGameById(gameId)?.toGame()

    /**
     * Fügt ein Spiel zur Wunschliste hinzu.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param game Das hinzuzufügende Spiel
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun addToWishlist(context: Context, game: Game): Resource<Unit> {
        return try {
                        wishlistGameDao.insertWishlistGame(game.toWishlistEntity())
                        Resource.Success(Unit)
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "insert",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "addToWishlist",
                                gameId = game.id,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Hinzufügen zur Wunschliste: ${e.message}"
                        )
                        Resource.Error(context.getString(R.string.error_add_favorite))
                }
    }

    /**
     * Entfernt ein Spiel aus der Wunschliste.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param gameId Die ID des zu entfernenden Spiels
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun removeFromWishlist(context: Context, gameId: Int): Resource<Unit> {
        return try {
                        wishlistGameDao.removeWishlistGame(gameId)
                        Resource.Success(Unit)
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "delete",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "removeFromWishlist",
                                gameId = gameId,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Entfernen aus Wunschliste: ${e.message}"
                        )
                        Resource.Error(context.getString(R.string.error_remove_favorite))
                }
    }

    /**
     * Fügt ein Spiel hinzu oder entfernt es aus der Wunschliste (Toggle).
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param game Das Spiel, das hinzugefügt oder entfernt werden soll
     * @return Resource mit true, wenn hinzugefügt, false wenn entfernt, oder Fehler
     */
    suspend fun toggleWishlist(context: Context, game: Game): Resource<Boolean> {
        return try {
                        val isInWishlist = wishlistGameDao.isInWishlist(game.id)
                        if (isInWishlist) {
                                wishlistGameDao.removeWishlistGame(game.id)
                                Resource.Success(false)
                        } else {
                                wishlistGameDao.insertWishlistGame(game.toWishlistEntity())
                                Resource.Success(true)
                        }
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "toggle",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "toggleWishlist",
                                gameId = game.id,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Umschalten der Wunschliste: ${e.message}"
                        )
                        Resource.Error(context.getString(R.string.error_add_favorite))
                }
    }

    /**
     * Löscht alle Spiele aus der Wunschliste.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun clearAllWishlistGames(context: Context): Resource<Unit> {
        return try {
                        wishlistGameDao.clearAllWishlistGames()
                        Resource.Success(Unit)
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "deleteAll",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "clearAllWishlistGames",
                                gameId = -1,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Leeren der Wunschliste: ${e.message}"
                        )
                        Resource.Error(context.getString(R.string.error_remove_favorite))
                }
    }

    /**
     * Gibt die Anzahl der Spiele in der Wunschliste zurück.
     * @return Anzahl der Spiele
     */
        suspend fun getWishlistCount(): Int = getAllWishlistGames().first().size

    /**
     * Sucht Spiele in der Wunschliste anhand eines Suchbegriffs.
     * @param query Suchbegriff für den Spieletitel
     * @return Flow mit einer Liste der gefundenen Spiele
     */
        fun searchWishlistGames(query: String): Flow<List<Game>> =
                wishlistGameDao.searchWishlistGames(query).map {
                        it.map { entity -> entity.toGame() }
                }

    /**
     * Exportiert die Wunschliste als JSON an eine angegebene Uri.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param uri Ziel-Uri für den Export
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun exportWishlistToUri(context: Context, uri: Uri): Resource<Unit> {
        return try {
                        val wishlist = getAllWishlistGames().first()
                        val json = adapter.toJson(wishlist)
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                OutputStreamWriter(outputStream).use { writer ->
                                        writer.write(json)
                                }
                        }
                                ?: throw Exception("Konnte OutputStream nicht öffnen")
                        Resource.Success(Unit)
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "export",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "exportWishlistToUri",
                                gameId = -1,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Export (Uri): ${e.localizedMessage}",
                                e
                        )
                        Resource.Error(context.getString(R.string.wishlist_export_error))
                }
    }

    /**
     * Importiert eine Wunschliste im JSON-Format von einer Uri.
     * @param context Anwendungskontext (für Fehlermeldungen)
     * @param uri Quell-Uri für den Import
     * @return Resource mit Erfolg oder Fehler
     */
    suspend fun importWishlistFromUri(context: Context, uri: Uri): Resource<Unit> {
        return try {
                        val json =
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        InputStreamReader(inputStream).use { reader ->
                                                reader.readText()
                                        }
                                }
                                        ?: throw Exception("Konnte InputStream nicht öffnen")
                        val games = adapter.fromJson(json) ?: emptyList()
                        games.forEach { addToWishlist(context, it) }
                        Resource.Success(Unit)
                } catch (e: Exception) {
                        CrashlyticsHelper.recordDatabaseError(
                                "import",
                                Constants.WISHLIST_GAME_TABLE,
                                e.localizedMessage ?: e.toString()
                        )
                        CrashlyticsHelper.recordWishlistError(
                                "importWishlistFromUri",
                                gameId = -1,
                                error = e.localizedMessage ?: e.toString()
                        )
                        AppLogger.e(
                                "WishlistRepository",
                                "Fehler beim Import (Uri): ${e.localizedMessage}",
                                e
                        )
                        Resource.Error(context.getString(R.string.wishlist_import_error))
                }
    }
}
