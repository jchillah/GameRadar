package de.syntax_institut.androidabschlussprojekt

import android.*
import android.content.*
import android.content.pm.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.core.content.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*

/**
 * Einstiegspunkt der App. Setzt das UI-Root und behandelt Deep Links und Berechtigungen. Optimiert
 * für Performance und minimale Frame-Drops.
 */
class MainActivity : ComponentActivity() {
    // Launcher für die Notification-Berechtigung
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Berechtigung erteilt: App kann Benachrichtigungen senden
                AppLogger.d("MainActivity", "Notification-Berechtigung erteilt")
            } else {
                // Berechtigung verweigert: Nutzer informieren
                AppLogger.d("MainActivity", "Notification-Berechtigung verweigert")
            }
        }

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
        askNotificationPermission()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /** Fragt die Notification-Berechtigung ab Android 13+ zur Laufzeit an. */
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Berechtigung bereits erteilt
                    AppLogger.d("MainActivity", "Notification-Berechtigung bereits erteilt")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // TODO: Zeige dem Nutzer eine Erklärung, warum die Berechtigung benötigt wird
                    // Für Demo direkt anfragen:
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    // Direkt anfragen
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
