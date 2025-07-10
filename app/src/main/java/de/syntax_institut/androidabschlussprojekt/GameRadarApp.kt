package de.syntax_institut.androidabschlussprojekt

import android.app.*
import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.di.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*

/**
 * GameRadarApp
 * Initialisiert Koin für Dependency Injection.
 */
class GameRadarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GameRadarApp)
            modules(networkModule)
            modules(repositoryModule)
            modules(useCaseModule)
            modules(viewModelModule)
        }
        createNewGamesNotificationChannel(this)
    }

    private fun createNewGamesNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_desc)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}