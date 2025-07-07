package de.syntax_institut.androidabschlussprojekt

import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.os.*
import androidx.activity.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.core.app.*
import androidx.core.net.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import org.koin.android.ext.android.*

/**
 * MainActivity
 * Einstiegspunkt der App.
 */
class MainActivity : ComponentActivity() {
    private val gameRepository: GameRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // TODO: Handle permission granted or denied with a Snackbar
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent {
            val navController = rememberNavController()
            var pendingSlug by remember { mutableStateOf<String?>(null) }
            var pendingGameId by remember { mutableStateOf<Int?>(null) }
            val snackbarHostState = remember { SnackbarHostState() }
            MyAppTheme {
                Box(Modifier.fillMaxSize()) {
                    App(
                    )
                    SnackbarHost(hostState = snackbarHostState)
                }
            }
            // Deep Link Intent beim Start prüfen
            LaunchedEffect(Unit) {
                val slug =
                    intent.data?.takeIf { it.scheme == "myapp" && it.host == "game" }?.lastPathSegment
                if (slug != null) {
                    pendingSlug = slug
                }
            }
            // Deep Link Intent bei erneutem Öffnen prüfen
            LaunchedEffect(intent) {
                val slug =
                    intent.data?.takeIf { it.scheme == "myapp" && it.host == "game" }?.lastPathSegment
                if (slug != null) {
                    pendingSlug = slug
                }
            }
            // Wenn ein Slug gesetzt wurde, suche die GameId
            LaunchedEffect(pendingSlug) {
                pendingSlug?.let { slug ->
                    val id = gameRepository.getGameIdBySlug(slug)
                    if (id != null) {
                        pendingGameId = id
                    } else {
                        snackbarHostState.showSnackbar("Spiel nicht gefunden: $slug")
                    }
                    pendingSlug = null
                }
            }
            LaunchedEffect(pendingGameId) {
                pendingGameId?.let { id ->
                    navController.navigate(
                        de.syntax_institut.androidabschlussprojekt.navigation.Routes.detail(id)
                    )
                    pendingGameId = null
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "new_games_channel",
            "Neue Spiele",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Benachrichtigungen über neue Spiele"
            enableLights(true)
            lightColor = Color.GREEN
            enableVibration(true)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNewGameNotification(
        context: Context,
        gameTitle: String,
        gameSlug: String,
        gameId: Int,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            data = "myapp://game/$gameSlug".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, "new_games_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Neues Spiel verfügbar!")
            .setContentText("Schau dir '$gameTitle' jetzt an.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(gameId, builder.build())
        }
    }
}