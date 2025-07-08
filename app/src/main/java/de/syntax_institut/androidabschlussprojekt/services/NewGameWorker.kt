package de.syntax_institut.androidabschlussprojekt.services

import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import androidx.core.app.*
import androidx.core.net.*
import androidx.work.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.core.component.*

class NewGameWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val gameRepository: GameRepository by inject()

    override suspend fun doWork(): Result {
        try {
            AppLogger.d(
                Constants.NEW_GAME_WORKER_NAME,
                "[DEBUG] ${Constants.NEW_GAME_WORKER_NAME} gestartet"
            )

            // Echte Logik: Suche nach neuen Spielen (nach ID und Slug)
            val prefs =
                applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val newGames = gameRepository.checkForNewGamesAndUpdatePrefs(prefs, count = 10)

            AppLogger.d(
                Constants.NEW_GAME_WORKER_NAME,
                "[DEBUG] ${newGames.size} neue Spiele gefunden"
            )

            newGames.forEach { game ->
                sendNewGameNotification(
                    applicationContext,
                    game.title,
                    game.slug,
                    game.id
                )
            }

            AppLogger.i(
                Constants.NEW_GAME_WORKER_NAME,
                "${Constants.NEW_GAME_WORKER_NAME} erfolgreich abgeschlossen"
            )
            return Result.success()
        } catch (e: Exception) {
            AppLogger.e(
                Constants.NEW_GAME_WORKER_NAME,
                "Fehler im ${Constants.NEW_GAME_WORKER_NAME}",
                e
            )
            return Result.failure()
        }
    }

    private fun sendNewGameNotification(
        context: Context,
        gameTitle: String,
        gameSlug: String,
        gameId: Int,
    ) {
        try {
            // Überprüfe Notification-Berechtigung für Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    AppLogger.d(
                        Constants.NEW_GAME_WORKER_NAME,
                        "[DEBUG] Keine Notification-Berechtigung für: $gameTitle"
                    )
                    return
                }
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Erstelle Notification Channel für Android 8.0+
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = Constants.NOTIFICATION_CHANNEL_DESC
            }
            notificationManager.createNotificationChannel(channel)

            // Erstelle Intent für die DetailScreen
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                data = "myapp://game/$gameSlug".toUri()
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                gameId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Erstelle Notification
            val notification =
                NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(Constants.NOTIFICATION_TITLE_NEW_GAME)
                .setContentText(gameTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            // Sende Notification
            notificationManager.notify(gameId, notification)

            AppLogger.d(
                Constants.NEW_GAME_WORKER_NAME,
                "[DEBUG] Notification gesendet für: $gameTitle"
            )

        } catch (e: Exception) {
            AppLogger.e(Constants.NEW_GAME_WORKER_NAME, "Fehler beim Senden der Notification", e)
        }
    }
} 