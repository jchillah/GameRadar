package de.syntax_institut.androidabschlussprojekt.services

import android.content.*
import androidx.work.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import org.koin.core.component.*

class NewGameWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val gameRepository: GameRepository by inject()

    override suspend fun doWork(): Result {
        // Echte Logik: Suche nach neuen Spielen (nach ID und Slug)
        val prefs =
            applicationContext.getSharedPreferences("gameradar_settings", Context.MODE_PRIVATE)
        val newGames = gameRepository.checkForNewGamesAndUpdatePrefs(prefs, count = 10)
        newGames.forEach { game ->
            MainActivity().sendNewGameNotification(
                applicationContext,
                game.title,
                game.slug,
                game.id
            )
        }
        return Result.success()
    }
} 