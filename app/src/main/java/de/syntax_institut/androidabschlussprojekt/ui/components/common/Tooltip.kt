package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*

/**
 * Tooltip-Komponente für Hilfestellungen und zusätzliche Informationen.
 * Folgt Material Design Guidelines für Tooltips.
 */
@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showTooltip = !showTooltip },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Info anzeigen",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(
            visible = showTooltip,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .offset(y = 32.dp)
                    .widthIn(max = 200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

/**
 * Erweiterte Tooltip-Komponente mit Positionierung.
 */
@Composable
fun PositionedTooltip(
    text: String,
    modifier: Modifier = Modifier,
    position: TooltipPosition = TooltipPosition.BOTTOM,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showTooltip = !showTooltip },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = "Hilfe anzeigen",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(
            visible = showTooltip,
            enter = fadeIn() + when (position) {
                TooltipPosition.TOP -> slideInVertically { -it }
                TooltipPosition.BOTTOM -> slideInVertically { it }
                TooltipPosition.LEFT -> slideInHorizontally { -it }
                TooltipPosition.RIGHT -> slideInHorizontally { it }
            },
            exit = fadeOut() + when (position) {
                TooltipPosition.TOP -> slideOutVertically { -it }
                TooltipPosition.BOTTOM -> slideOutVertically { it }
                TooltipPosition.LEFT -> slideOutHorizontally { -it }
                TooltipPosition.RIGHT -> slideOutHorizontally { it }
            }
        ) {
            Card(
                modifier = Modifier
                    .offset(
                        x = when (position) {
                            TooltipPosition.LEFT -> (-200).dp
                            TooltipPosition.RIGHT -> 32.dp
                            else -> 0.dp
                        },
                        y = when (position) {
                            TooltipPosition.TOP -> (-100).dp
                            TooltipPosition.BOTTOM -> 32.dp
                            else -> 0.dp
                        }
                    )
                    .widthIn(max = 200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

enum class TooltipPosition {
    TOP, BOTTOM, LEFT, RIGHT
}

@Preview(showBackground = true)
@Composable
fun TooltipPreview() {
    Tooltip(
        text = "Dies ist ein hilfreicher Tooltip mit zusätzlichen Informationen."
    )
}

@Preview(showBackground = true)
@Composable
fun PositionedTooltipPreview() {
    PositionedTooltip(
        text = "Tooltip mit Positionierung",
        position = TooltipPosition.BOTTOM
    )
} 