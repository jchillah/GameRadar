package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun ShareButton(
    gameTitle: String,
    gameUrl: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shareText = stringResource(R.string.detail_share_text, gameTitle)
    val shareLink = stringResource(R.string.detail_share_link, gameUrl)
    val chooserTitle = stringResource(R.string.share_games)
    Button(
        modifier = modifier,
        onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$shareText\n$shareLink")
            }
            val chooser = Intent.createChooser(intent, chooserTitle)
            context.startActivity(chooser)
        }
    ) {
        Icon(Icons.Default.Share, contentDescription = null)
        // überlagert UI etwas
        // Spacer(modifier = Modifier.width(8.dp)
        // Text(text = chooserTitle)
    }
}

@Preview(showBackground = true)
@Composable
fun ShareButtonPreview() {
    ShareButton(
        gameTitle = stringResource(R.string.preview_settings_button_title),
        gameUrl = stringResource(R.string.preview_game_image_url)
    )
}