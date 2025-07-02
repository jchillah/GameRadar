package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.CacheBanner
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.IntelligentCacheIndicator
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.NetworkErrorHandler

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    setDarkTheme: (Boolean) -> Unit,
    cacheSize: Int,
    maxCacheSize: Int = 1000,
    isOffline: Boolean,
    lastSyncTime: Long?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cache-Banner und Cache-Status
        CacheBanner(
            cacheSize = cacheSize,
            maxCacheSize = maxCacheSize,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        IntelligentCacheIndicator(
            isOffline = isOffline,
            cacheSize = cacheSize,
            lastSyncTime = lastSyncTime,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        NetworkErrorHandler(
            isOffline = isOffline,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text(
            "Einstellungen",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode", color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            val borderColor = MaterialTheme.colorScheme.onPrimary
            Box(
                modifier = Modifier
                    .border(width = 3.dp, color = borderColor, shape = MaterialTheme.shapes.medium)
                    .padding(2.dp)
            ) {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = setDarkTheme,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        uncheckedThumbColor = Color.White,
                        checkedTrackColor = borderColor.copy(alpha = 0.5f),
                        uncheckedTrackColor = borderColor.copy(alpha = 0.2f),
                        checkedBorderColor = borderColor,
                        uncheckedBorderColor = borderColor
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        isDarkTheme = true,
        setDarkTheme = {},
        cacheSize = 500,
        isOffline = true,
        maxCacheSize = 100000,
        lastSyncTime = System.currentTimeMillis(),
    )
}