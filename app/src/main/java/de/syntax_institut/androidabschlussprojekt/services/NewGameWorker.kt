package de.syntax_institut.androidabschlussprojekt.services

import android.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import androidx.core.app.*
import androidx.work.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
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

            if (!isNotificationsEnabled(applicationContext)) {
                AppLogger.d(
                    Constants.NEW_GAME_WORKER_NAME,
                    "[DEBUG] Notifications deaktiviert – Abbruch"
                )
                return Result.success()
            }

            val prefs =
                applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val newGames = gameRepository.checkForNewGamesAndUpdatePrefs(prefs, count = 10)

            AppLogger.d(
                Constants.NEW_GAME_WORKER_NAME,
                "[DEBUG] ${newGames.size} neue Spiele gefunden"
            )

            if (newGames.isNotEmpty()) {
                sendNewGamesNotification(applicationContext, newGames)
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

    private fun isNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true)
    }

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun sendNewGamesNotification(context: Context, newGames: List<Game>) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    AppLogger.d(
                        Constants.NEW_GAME_WORKER_NAME,
                        "[DEBUG] Keine Notification-Berechtigung für neue Spiele"
                    )
                    return
                }
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val pendingIntent = buildPendingIntent(context)
            // Bündel-Notification für mehrere neue Spiele
            val inboxStyle = NotificationCompat.InboxStyle()
            for (game in newGames) {
                inboxStyle.addLine(game.title)
            }
            val notification =
                NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_dialog_info)
                    .setContentTitle(context.getString(R.string.notification_new_games_title))
                    .setContentText(
                        context.getString(
                            R.string.notification_new_games_text,
                            newGames.size
                        )
                    )
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(Constants.NEW_GAME_NOTIFICATION_ID, notification.build())
            AppLogger.d(
                Constants.NEW_GAME_WORKER_NAME,
                "[DEBUG] Notification gesendet für ${newGames.size} neue Spiele"
            )
        } catch (e: Exception) {
            AppLogger.e(Constants.NEW_GAME_WORKER_NAME, "Fehler beim Senden der Notification", e)
        }
    }
} 