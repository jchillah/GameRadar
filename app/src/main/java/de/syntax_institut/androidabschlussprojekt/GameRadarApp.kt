package de.syntax_institut.androidabschlussprojekt

import android.app.*
import de.syntax_institut.androidabschlussprojekt.di.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import de.syntax_institut.androidabschlussprojekt.utils.LocaleManager
import org.koin.android.ext.koin.*
import org.koin.core.context.*

/** GameRadarApp Initialisiert Koin, Firebase Services und Performance-Monitoring. */
class GameRadarApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Koin Dependency Injection initialisieren
        startKoin {
            androidLogger()
            androidContext(this@GameRadarApp)
            modules(
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }

        // Performance-Monitoring für App-Start
        PerformanceMonitor.startTimer("app_startup")
        PerformanceMonitor.incrementEventCounter("app_launches")

        // Systemsprache für LocaleManager verwenden
        val systemLocale = LocaleManager.getSystemLocale(this)
        AppLogger.d("GameRadarApp", "App started with system locale: ${systemLocale.language}")

        // Crashlytics Custom Keys setzen
        CrashlyticsHelper.setCustomKey("app_created", true)
        CrashlyticsHelper.setCustomKey("system_language", systemLocale.language)
        CrashlyticsHelper.setCustomKey(
            "available_languages_count",
            LocaleManager.getAvailableLanguagesForUI().size
        )

        // Analytics-Tracking
        AppAnalytics.trackEvent("app_started", mapOf("system_language" to systemLocale.language))

        // App-Start-Performance tracken
        val startupDuration = PerformanceMonitor.endTimer("app_startup")
        PerformanceMonitor.trackAppStart(startupDuration, true)

        AppLogger.d("GameRadarApp", "GameRadarApp mit Koin initialisiert")
    }

    override fun onTerminate() {
        super.onTerminate()

        // Performance-Statistiken abrufen und loggen vor dem Beenden
        val performanceStats = PerformanceMonitor.getPerformanceStats()
        AppLogger.d("GameRadarApp", "Final Performance Stats: $performanceStats")

        // Performance-Monitoring-Daten bereinigen
        PerformanceMonitor.cleanupAndSendSummary()

        AppLogger.d("GameRadarApp", "App beendet")
    }
}
