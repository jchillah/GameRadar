package de.syntax_institut.androidabschlussprojekt

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*

/**
 * Einstiegspunkt der App. Setzt das UI-Root und behandelt Deep Links und Berechtigungen. Optimiert
 * für Performance und minimale Frame-Drops.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Performance-Monitoring für Activity-Start
        PerformanceMonitor.startTimer("main_activity_startup")
        PerformanceMonitor.incrementEventCounter("main_activity_opened")

        // Systemsprache für LocaleManager verwenden (asynchron)
        lifecycleScope.launch(Dispatchers.IO) {
            val systemLocale = LocaleManager.getSystemLocale(this@MainActivity)
            AppLogger.d("MainActivity", "System locale: ${systemLocale.language}")

            // Crashlytics Custom Keys setzen
            CrashlyticsHelper.setCustomKey("main_activity_created", true)
            CrashlyticsHelper.setCustomKey("system_language", systemLocale.language)

            // Analytics-Tracking
            AppAnalytics.trackEvent(
                "main_activity_opened",
                mapOf("system_language" to systemLocale.language)
            )
        }

        // UI sofort setzen, um Frame-Drops zu minimieren
        setContent { AppRoot() }

        val startupDuration = PerformanceMonitor.endTimer("main_activity_startup")
        PerformanceMonitor.trackUiRendering("MainActivity", startupDuration)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
