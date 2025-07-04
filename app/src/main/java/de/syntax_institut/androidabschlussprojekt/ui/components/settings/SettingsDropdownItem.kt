package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*

@Composable
internal fun SettingsDropdownItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }
    var iconOffsetX by remember { mutableFloatStateOf(0f) }
    var iconWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = selectedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        iconOffsetX = coordinates.positionInParent().x
                        iconWidth = coordinates.size.width
                    }
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            offset = with(density) {
                DpOffset(
                    x = (iconOffsetX + iconWidth).toDp() - 200.dp, // 200.dp = Menübreite, ggf. anpassen
                    y = 0.dp
                )
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsDropdownItemPreview() {
    SettingsDropdownItem(
        icon = Icons.Filled.Language,
        title = "Sprache",
        subtitle = "App-Sprache wählen",
        selectedValue = "Deutsch",
        onValueChange = {},
        options = listOf("Deutsch", "Englisch", "Französisch")
    )
}