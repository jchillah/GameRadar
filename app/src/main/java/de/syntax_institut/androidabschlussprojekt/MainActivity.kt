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
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.*

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

    private val settingsRepository: SettingsRepository by inject()
    private var currentLanguage: String = "system"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PerformanceMonitor.startTimer("main_activity_startup")
        PerformanceMonitor.incrementEventCounter("main_activity_opened")

        // Set the correct locale based on saved settings
        lifecycleScope.launch {
            settingsRepository.language.collect { newLanguage ->
                if (newLanguage != currentLanguage) {
                    AppLogger.d(
                        "MainActivity",
                        "Language changed from $currentLanguage to $newLanguage"
                    )
                    currentLanguage = newLanguage

                    // Update locale and recreate activity
                    (application as GameRadarApp).updateLocale(this@MainActivity, newLanguage)

                    // Recreate activity to apply language changes
                    recreate()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val systemLocale = LocaleManager.getSystemLocale(this@MainActivity)
            AppLogger.d("MainActivity", "System locale: ${systemLocale.language}")
            AppLogger.d("MainActivity", "App language: $currentLanguage")

            CrashlyticsHelper.setCustomKey("main_activity_created", true)
            CrashlyticsHelper.setCustomKey("system_language", systemLocale.language)
            currentLanguage?.let {
                CrashlyticsHelper.setCustomKey("app_language", it)
            }

            AppAnalytics.trackEvent(
                "main_activity_opened",
                mapOf(
                    "system_language" to systemLocale.language,
                    "app_language" to (currentLanguage ?: "system")
                )
            )
        }

        // Initial language setup
        lifecycleScope.launch {
            val savedLanguage = settingsRepository.language.value
            if (savedLanguage != "system") {
                (application as GameRadarApp).updateLocale(this@MainActivity, savedLanguage)
            }
            currentLanguage = savedLanguage
        }

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
                    AppLogger.d("MainActivity", "Notification-Berechtigung bereits erteilt")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle(R.string.notification_permission_title)
                        .setMessage(R.string.notification_permission_rationale)
                        .setPositiveButton(R.string.ok) { e, msg ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
