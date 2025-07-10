package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.models.*

@Composable
fun SectionDataSync(
    modifier: Modifier = Modifier,
    autoRefreshEnabled: Boolean,
    imageQuality: ImageQuality,
    onAutoRefreshChange: (Boolean) -> Unit,
    onImageQualityChange: (ImageQuality) -> Unit,
) {
    val imageQualityOptions = listOf(
        DropdownOption(
            ImageQuality.LOW.name,
            stringResource(R.string.image_quality_low),
            Icons.Default.HighQuality
        ),
        DropdownOption(
            ImageQuality.MEDIUM.name,
            stringResource(R.string.image_quality_medium),
            Icons.Default.HighQuality
        ),
        DropdownOption(
            ImageQuality.HIGH.name,
            stringResource(R.string.image_quality_high),
            Icons.Default.HighQuality
        )
    )

    Column(modifier = modifier) {
        // Auto-Refresh Einstellung
        SettingsSwitchItem(
            icon = Icons.Default.Sync,
            title = stringResource(R.string.auto_refresh),
            subtitle = stringResource(R.string.auto_refresh_description),
            checked = autoRefreshEnabled,
            onCheckedChange = onAutoRefreshChange
        )

        // Bildqualität Einstellung
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HighQuality,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.image_quality),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.image_quality_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedDropdown(
                selectedValue = imageQualityOptions.firstOrNull { it.value == imageQuality.name }?.label ?: stringResource(R.string.image_quality_high),
                onValueChange = { selectedValue ->
                    val quality = ImageQuality.entries.find { it.name == selectedValue }
                        ?: ImageQuality.HIGH
                    onImageQualityChange(quality)
                },
                modifier = Modifier.padding(top = 8.dp),
                options = imageQualityOptions
            )
        }
    }
} 