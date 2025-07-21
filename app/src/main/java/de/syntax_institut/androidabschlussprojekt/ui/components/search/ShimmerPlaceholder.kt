package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.valentinilk.shimmer.*

/**
 * Animierter Shimmer-Platzhalter für Loading-Zustände.
 *
 * Features:
 * - Shimmer-Animation für bessere UX
 * - Platzhalter für Spielbild und Text
 * - Material3 Theme-Integration
 * - Responsive Layout
 * - Window-basierte Shimmer-Grenzen
 * - Optimierte Performance
 *
 * @param modifier Modifier für das Layout
 */
@Composable
fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .shimmer(shimmerInstance)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(16.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerPlaceholderPreview() {
    ShimmerPlaceholder()
} 