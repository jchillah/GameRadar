package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.core.net.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.utils.*

@Composable
fun WebsiteSection(website: String?, gameId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    if (website.isNullOrBlank() ||
        !(website.startsWith("http://") || website.startsWith("https://"))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp), contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.game_no_website),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, website.toUri())
                    context.startActivity(intent)
                    AppAnalytics.trackUserAction("website_opened", gameId)
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    stringResource(R.string.game_website),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
