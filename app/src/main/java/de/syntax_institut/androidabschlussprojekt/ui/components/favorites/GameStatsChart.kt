package de.syntax_institut.androidabschlussprojekt.ui.components.favorites

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

/**
 * Zeigt ein animiertes horizontales Balkendiagramm der Top-Genres aus den Favoriten.
 *
 * Features:
 * - Top 5 Genres basierend auf Häufigkeit
 * - Animierte Balken mit Prozentanzeige
 * - Theme-adaptive Farben (hell/dunkel)
 * - Horizontales Scrollen bei vielen Genres
 * - Material3 Card-Design
 * - Responsive Layout mit Mindest-/Maximalbreite
 * - Prozentuale Verteilung der Genres
 *
 * Design:
 * - Balken mit abgerundeten Ecken
 * - Icon und Titel für bessere UX
 * - Farbkodierung je nach Theme
 * - Kompakte Darstellung mit Werten
 *
 * @param genreCounts Map mit Genre-Namen als Schlüssel und deren Anzahl als Wert
 * @param modifier Modifier für das Layout
 */
@Composable
fun GameStatsChart(genreCounts: Map<String, Int>, modifier: Modifier = Modifier) {
    val sorted = genreCounts.entries.sortedByDescending { it.value }.take(5)
    val total = genreCounts.values.sum().coerceAtLeast(1)
    // Dynamische Bar-Farbe je nach Theme
    val isDark = isSystemInDarkTheme()
    val barColor = if (isDark) Color(0xFF00C853) else Color(0xFFFFD700)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Top Genres",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier =
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.widthIn(min = 220.dp, max = 400.dp)) {
                sorted.forEachIndexed { idx, (label, value) ->
                    val percent = (value * 100f / total).toInt()
                    val animatedPercent by
                    animateFloatAsState(targetValue = percent / 100f, label = "barAnim$idx")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = label, fontSize = 14.sp, modifier = Modifier.width(100.dp))
                        Box(
                            modifier =
                                Modifier
                                    .height(20.dp)
                                    .weight(1f)
                                    .background(
                                        color =
                                            MaterialTheme.colorScheme
                                                .onSurfaceVariant.copy(
                                                    alpha = 0.08f
                                                ),
                                        shape = MaterialTheme.shapes.small
                                    )
                        ) {
                            Canvas(
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(animatedPercent)
                            ) {
                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset.Zero,
                                    size = size,
                                    cornerRadius =
                                        androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$value (${percent}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
