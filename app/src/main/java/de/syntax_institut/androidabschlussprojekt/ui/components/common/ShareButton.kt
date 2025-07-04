package de.syntax_institut.androidabschlussprojekt.ui.components.common

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*

@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    gameTitle: String,
    gameUrl: String? = null,
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            val shareText = if (gameUrl != null) {
                "Schau dir '$gameTitle' an: $gameUrl"
            } else {
                "Schau dir '$gameTitle' an!"
            }

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            context.startActivity(Intent.createChooser(intent, "Spiel teilen"))
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Spiel teilen",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShareButtonPreview() {
    ShareButton(gameTitle = "Beispielspiel", gameUrl = "https://example.com")
}