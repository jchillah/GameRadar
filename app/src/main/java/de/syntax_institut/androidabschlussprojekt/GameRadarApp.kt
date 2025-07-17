package de.syntax_institut.androidabschlussprojekt

import android.app.*
import android.content.*
import android.os.*
import androidx.core.content.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.di.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*

/** GameRadarApp Initialisiert Koin für Dependency Injection und Firebase Services. */
class GameRadarApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase Services initialisieren
        AppAnalytics.init(this)
        CrashlyticsHelper.init()
        CrashlyticsHelper.setAppInfo()

        // Anonyme User-ID setzen (für Crashlytics-Tracking)
        val anonymousUserId = generateAnonymousUserId()
        CrashlyticsHelper.setUserId(anonymousUserId)
        CrashlyticsHelper.setCustomKey("app_version", BuildConfig.VERSION_NAME)
        CrashlyticsHelper.setCustomKey("build_type", if (BuildConfig.DEBUG) "debug" else "release")

        // Performance-Monitoring starten
        PerformanceMonitor.startTimer("app_startup")

        // Koin für Dependency Injection initialisieren
        startKoin {
            androidContext(this@GameRadarApp)
            modules(networkModule)
            modules(repositoryModule)
            modules(useCaseModule)
            modules(viewModelModule)
        }

        // Notification Channel erstellen
        createNewGamesNotificationChannel(this)

        // App-Start-Event tracken
        AppAnalytics.trackEvent(
            "app_launched",
            mapOf(
                "version" to BuildConfig.VERSION_NAME,
                "build_type" to if (BuildConfig.DEBUG) "debug" else "release"
            )
        )

        // Performance-Monitoring beenden
        PerformanceMonitor.endTimer("app_startup")
        PerformanceMonitor.trackAppStart(SystemClock.elapsedRealtime(), true)

        AppLogger.d("GameRadarApp", "App erfolgreich initialisiert")

        AppOpenAdManager.init(
            app = this,
            adUnitId = "ca-app-pub-7269049262039376/9765911397",
            adsEnabled = true,
            isProUser = false,
            analyticsEnabled = true
        )
    }

    private fun createNewGamesNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    description = context.getString(R.string.notification_channel_desc)
                }
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Generiert eine anonyme User-ID für Crashlytics-Tracking. Diese ID bleibt für die gesamte
     * App-Installation konstant.
     */
    private fun generateAnonymousUserId(): String {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userIdKey = "anonymous_user_id"

        var userId = prefs.getString(userIdKey, null)
        if (userId == null) {
            userId = "user_${System.currentTimeMillis()}_${(0..9999).random()}"
            prefs.edit { putString(userIdKey, userId) }
        }

        return userId
    }
}
