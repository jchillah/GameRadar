package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
fun GameStatsCard(
    playtime: Int?,
    metacritic: Int?,
    userRating: Float,
    onRatingChanged: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Spielstatistiken",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                playtime?.let { time ->
                    StatItem(
                        icon = Icons.Filled.Timer,
                        label = "Spielzeit",
                        value = "${time}h",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                metacritic?.let { score ->
                    StatItem(
                        icon = Icons.Filled.Star,
                        label = "Metacritic",
                        value = score.toString(),
                        color = when {
                            score >= 80 -> Color(0xFF4CAF50)
                            score >= 60 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }

                GameRatingButton(
                    rating = userRating,
                    onRatingChanged = onRatingChanged
                )
            }
        }
    }
}