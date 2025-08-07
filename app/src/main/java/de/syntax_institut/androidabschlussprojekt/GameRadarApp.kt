package de.syntax_institut.androidabschlussprojekt

import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import android.view.*
import androidx.appcompat.app.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.*
import de.syntax_institut.androidabschlussprojekt.ads.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.di.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import de.syntax_institut.androidabschlussprojekt.utils.LocaleManager
import org.koin.android.ext.koin.*
import org.koin.core.context.*
import java.util.*

/** GameRadarApp Initialisiert Koin, Firebase Services und Performance-Monitoring. */
class GameRadarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidContext(this@GameRadarApp)
            modules(
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }

        // Initialize AdMob
        MobileAds.initialize(this) {
            AppLogger.d("GameRadarApp", "AdMob initialized successfully")
        }

        // Echte Ads aktivieren für Produktion
        AdMobManager(this).forceUseRealAds()

        // Add test device for development (comment out for production)
        // val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
        // val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        // MobileAds.setRequestConfiguration(configuration)
        
        // Setze das korrekte Locale, falls bereits eine Einstellung gespeichert ist
        val settingsRepository = SettingsRepository(applicationContext)
        val savedLanguage = settingsRepository.language.value

        if (savedLanguage != "system") {
            updateLocale(this, savedLanguage)
        }

        // Koin Dependency Injection initialisieren
        // startKoin {
        //     androidLogger()
        //     androidContext(this@GameRadarApp)
        //     modules(
        //         networkModule,
        //         repositoryModule,
        //         useCaseModule,
        //         viewModelModule
        //     )
        // }

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
        CrashlyticsHelper.setCustomKey("saved_language", savedLanguage)

        // Analytics-Tracking
        AppAnalytics.trackEvent(
            "app_started", mapOf(
                "system_language" to systemLocale.language,
                "app_language" to savedLanguage
            )
        )

        // App-Start-Performance tracken
        val startupDuration = PerformanceMonitor.endTimer("app_startup")
        PerformanceMonitor.trackAppStart(startupDuration, true)

        // Initialize Mobile Ads SDK
        initializeMobileAds()

        AppLogger.d("GameRadarApp", "GameRadarApp mit Koin und Mobile Ads initialisiert")
    }

    /**
     * Aktualisiert das Locale der App
     */
    fun updateLocale(context: Context, languageCode: String) {
        // Verwende moderne Locale API statt deprecated Constructor
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.forLanguageTag(languageCode)
        } else {
            @Suppress("DEPRECATION")
            Locale(languageCode)
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        
        // Update configuration based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            // Verwende createConfigurationContext() statt updateConfiguration()
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            // Nur für ältere Android-Versionen (API < 17)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        // Update the application context nur für ältere Android-Versionen
        if (context != this && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        // Set RTL layout direction based on language
        AppCompatDelegate.setDefaultNightMode(
            if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    /**
     * Erstellt einen neuen Context mit der angegebenen Sprache
     */
    fun createLocalizedContext(context: Context, languageCode: String): Context {
        return LocaleManager.createLocalizedContext(context, languageCode)
    }

    /**
     * Initializes the Mobile Ads SDK.
     * This should be called as early as possible in the app's lifecycle.
     */
    private fun initializeMobileAds() {
        val startTime = System.currentTimeMillis()

        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(initializationStatus: InitializationStatus) {
                val initializationTime = System.currentTimeMillis() - startTime
                val statusMap = mutableMapOf<String, String>()

                // Log the status of each adapter
                for ((adapter, status) in initializationStatus.adapterStatusMap) {
                    val state = when (status.initializationState) {
                        AdapterStatus.State.READY -> "READY"
                        AdapterStatus.State.NOT_READY -> "NOT_READY"
                        else -> "UNKNOWN"
                    }

                    statusMap[adapter] = "$state - ${status.description}"

                    when (status.initializationState) {
                        AdapterStatus.State.READY -> {
                            AppLogger.d("MobileAds", "Adapter $adapter is ready")
                        }

                        AdapterStatus.State.NOT_READY -> {
                            AppLogger.w(
                                "MobileAds",
                                "Adapter $adapter is not ready: ${status.description}"
                            )
                        }
                    }
                }

                // Log the initialization time and status
                AppLogger.d(
                    "MobileAds",
                    "Initialization completed in ${initializationTime}ms. Status: $statusMap"
                )

                // Track the initialization time
                PerformanceMonitor.trackApiCall(
                    "mobile_ads_initialization",
                    initializationTime,
                    true
                )

                // Set custom keys for crash reporting
                CrashlyticsHelper.setCustomKey("ads_initialized", true)
                CrashlyticsHelper.setCustomKey("ads_initialization_time_ms", initializationTime)
                CrashlyticsHelper.setCustomKey("ads_adapters", statusMap.keys.joinToString())

                // Preload a rewarded ad for better user experience
                preloadRewardedAd()
            }
        })

        // Set global configuration
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("TEST_DEVICE_ID")) // Replace with actual test device ID
                .build()
        )
    }

    /**
     * Preloads a rewarded ad to have it ready when needed.
     */
    private fun preloadRewardedAd() {
        try {
            val rewardedAdManager = RewardedAdManager(this)
            rewardedAdManager.preloadAd()
            AppLogger.d("GameRadarApp", "Preloaded rewarded ad")
        } catch (e: Exception) {
            AppLogger.e("GameRadarApp", "Failed to preload rewarded ad: ${e.message}")
        }
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
