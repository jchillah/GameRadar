package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import android.net.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.WishlistGameMapper.toGame
import de.syntax_institut.androidabschlussprojekt.data.local.mapper.WishlistGameMapper.toWishlistEntity
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*
import java.io.*

class WishlistRepository(private val wishlistGameDao: WishlistGameDao) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, Game::class.java)
    private val adapter = moshi.adapter<List<Game>>(type)

    fun getAllWishlistGames(): Flow<List<Game>> =
        wishlistGameDao.getAllWishlistGames().map { it.map { entity -> entity.toGame() } }

    suspend fun isInWishlist(gameId: Int): Boolean = wishlistGameDao.isInWishlist(gameId)

    suspend fun getWishlistGameById(gameId: Int): Game? =
        wishlistGameDao.getWishlistGameById(gameId)?.toGame()

    suspend fun addToWishlist(game: Game): Resource<Unit> =
        try {
            wishlistGameDao.insertWishlistGame(game.toWishlistEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Hinzufügen zur Wunschliste: ${e.message}"
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }

    suspend fun removeFromWishlist(gameId: Int): Resource<Unit> =
        try {
            wishlistGameDao.removeWishlistGame(gameId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Entfernen aus Wunschliste: ${e.message}"
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }

    suspend fun toggleWishlist(game: Game): Resource<Boolean> =
        try {
            val isInWishlist = wishlistGameDao.isInWishlist(game.id)
            if (isInWishlist) {
                wishlistGameDao.removeWishlistGame(game.id)
                Resource.Success(false)
            } else {
                wishlistGameDao.insertWishlistGame(game.toWishlistEntity())
                Resource.Success(true)
            }
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Umschalten der Wunschliste: ${e.message}"
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }

    suspend fun clearAllWishlistGames(): Resource<Unit> =
        try {
            wishlistGameDao.clearAllWishlistGames()
            Resource.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Leeren der Wunschliste: ${e.message}"
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }

    suspend fun getWishlistCount(): Int = getAllWishlistGames().first().size

    fun searchWishlistGames(query: String): Flow<List<Game>> =
        wishlistGameDao.searchWishlistGames(query).map { it.map { entity -> entity.toGame() } }

    suspend fun exportWishlistToUri(context: Context, uri: Uri): Resource<Unit> =
        try {
            val wishlist = getAllWishlistGames().first()
            val json = adapter.toJson(wishlist)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer -> writer.write(json) }
            }
                ?: throw Exception("Konnte OutputStream nicht öffnen")
            Resource.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Export (Uri): ${e.localizedMessage}",
                e
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }

    suspend fun importWishlistFromUri(context: Context, uri: Uri): Resource<Unit> =
        try {
            val json =
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).use { reader -> reader.readText() }
                }
                    ?: throw Exception("Konnte InputStream nicht öffnen")
            val games = adapter.fromJson(json) ?: emptyList()
            games.forEach { addToWishlist(it) }
            Resource.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(
                "WishlistRepository",
                "Fehler beim Import (Uri): ${e.localizedMessage}",
                e
            )
            val msg = ErrorHandler.handle(e, "WishlistRepository")
            Resource.Error(msg)
        }
}
