package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*

/**
 * Zeigt die wichtigsten Statistiken eines Spiels (Spielzeit, Metacritic, User-Rating).
 */
@Composable
fun GameStatsCard(
    modifier: Modifier = Modifier,
    playtime: Int?,
    metacritic: Int?,
    userRating: Float,
    onRatingChanged: (Float) -> Unit = {},
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
                text = stringResource(R.string.detail_game_stats),
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
                        label = stringResource(R.string.detail_playtime),
                        value = "${time}h",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                metacritic?.let { score ->
                    StatItem(
                        icon = Icons.Filled.Star,
                        label = stringResource(R.string.detail_metacritic),
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

@Preview(showBackground = true)
@Composable
fun GameStatsCardPreview() {
    GameStatsCard(playtime = 12, metacritic = 85, userRating = 4.5f)
} 