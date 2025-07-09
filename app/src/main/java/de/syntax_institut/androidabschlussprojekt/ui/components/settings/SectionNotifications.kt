package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import android.app.*
import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.core.app.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.*

@Composable
fun SectionNotifications(
    notificationsEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSwitchItem(
            icon = Icons.Default.Notifications,
            title = Constants.UI_PUSH_NOTIFICATIONS,
            subtitle = Constants.UI_NEW_GAMES_AND_UPDATES,
            checked = notificationsEnabled,
            onCheckedChange = onCheckedChange
        )
        if (BuildConfig.DEBUG) {
            Button(
                onClick = {
                    // Test-Benachrichtigung direkt erstellen
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = Constants.NOTIFICATION_CHANNEL_ID

                    // Channel erstellen falls nicht vorhanden
                    val channel = NotificationChannel(
                        channelId,
                        Constants.NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)

                    val notification = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_dialog_info)
                        .setContentTitle(Constants.NOTIFICATION_TITLE_TEST)
                        .setContentText(Constants.NOTIFICATION_TEXT_TEST)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(999999, notification)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test: Neue Spiel-Benachrichtigung")
            }
        }
    }
} 