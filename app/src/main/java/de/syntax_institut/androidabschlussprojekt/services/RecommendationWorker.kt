package de.syntax_institut.androidabschlussprojekt.services

import android.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.os.*
import androidx.core.app.*
import androidx.work.*
import com.bumptech.glide.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

/** Worker für tägliche Empfehlungs-Notification basierend auf Favoriten- und Wishlist-Genres. */
class RecommendationWorker(
        appContext: Context,
        workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams), KoinComponent {

        private val getAllFavoritesUseCase: GetAllFavoritesUseCase by inject()
        private val getAllWishlistGamesUseCase: GetAllWishlistGamesUseCase by inject()
        private val gameRepository: GameRepository by inject()

        override suspend fun doWork(): Result {
                try {
                        // 1. Hole alle Favoriten und Wishlist-Spiele
                        val favorites = getAllFavoritesUseCase().first()
                        val wishlist = getAllWishlistGamesUseCase().first()
                        val allGames = favorites + wishlist
                        if (allGames.isEmpty()) return Result.success() // Keine Empfehlung möglich

                        // 2. Extrahiere alle Genres
                        val allGenres = allGames.flatMap { it.genres }.filter { it.isNotBlank() }
                        if (allGenres.isEmpty()) return Result.success()

                        // 3. Bestimme das häufigste oder ein zufälliges Genre
                        val genre =
                                allGenres
                                        .groupingBy { it }
                                        .eachCount()
                                        .maxByOrNull { it.value }
                                        ?.key
                                        ?: allGenres.random()

                        // 4. Hole ein zufälliges Spiel aus diesem Genre (API, Seite 1, 20 Spiele)
                        val response = gameRepository.getGamesByGenre(genre)
                        if (response.isEmpty()) return Result.success()
                        val recommendedGame = response.random()

                        // 5. Sende die Notification
                        sendRecommendationNotification(applicationContext, recommendedGame, genre)
                        return Result.success()
                } catch (e: Exception) {
                        return Result.failure()
                }
        }

        private fun sendRecommendationNotification(context: Context, game: Game, genre: String) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                                PackageManager.PERMISSION_GRANTED
                        ) {
                                return
                        }
                }
                val channelId = "recommendation_channel"
                val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                                NotificationManager
                // Channel anlegen
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel =
                                NotificationChannel(
                                        channelId,
                                        context.getString(
                                                R.string.notification_channel_recommendation
                                        ),
                                        NotificationManager.IMPORTANCE_DEFAULT
                                )
                        notificationManager.createNotificationChannel(channel)
                }
                // Intent zum Öffnen des Spiels (MainActivity mit Deep Link)
                val intent =
                        Intent(context, MainActivity::class.java).apply {
                                putExtra("game_id", game.id)
                                flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                val pendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                // Game-Header-Bild laden (synchron)
                val bitmap: Bitmap? =
                        try {
                                if (!game.imageUrl.isNullOrBlank()) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(game.imageUrl)
                                                .submit(600, 338) // 16:9
                                                .get()
                                } else null
                        } catch (_: Exception) {
                                null
                        } as Bitmap?
                val style =
                        if (bitmap != null) {
                                NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmap)
                                        .setSummaryText(
                                                context.getString(
                                                        R.string.notification_recommendation_text,
                                                        game.title
                                                )
                                        )
                        } else null
                val builder =
                        NotificationCompat.Builder(context, channelId)
                                .setSmallIcon(R.drawable.ic_dialog_info)
                                .setContentTitle(
                                        context.getString(
                                                R.string.notification_recommendation_title,
                                                genre
                                        )
                                )
                                .setContentText(
                                        context.getString(
                                                R.string.notification_recommendation_text,
                                                game.title
                                        )
                                )
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                if (style != null) builder.setStyle(style)
                notificationManager.notify(888888, builder.build())
        }
}
