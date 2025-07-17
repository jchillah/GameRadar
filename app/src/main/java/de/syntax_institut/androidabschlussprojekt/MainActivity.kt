package de.syntax_institut.androidabschlussprojekt

import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*

/**
 * Einstiegspunkt der App. Setzt das UI-Root und behandelt Deep Links und Berechtigungen. Keine
 * Business-Logik oder Service-Initialisierung!
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* Permission-Result-Handling ggf. mit Snackbar */ }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent { AppRoot() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /** Erstellt den Notification Channel für neue Spiele. */
    private fun createNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(
                "new_games",
                "Neue Spiele",
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    description = "Benachrichtigungen über neue Spiele"
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(true)
                }
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
