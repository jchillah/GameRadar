package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailModal(
    game: Game,
    onClose: () -> Unit,
    onShowDetails: () -> Unit,
    sheetState: SheetState,
) {
    ModalBottomSheet(onDismissRequest = onClose, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = game.title, style = MaterialTheme.typography.titleLarge)
            if (!game.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(game.imageUrl),
                    contentDescription = game.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = game.description ?: stringResource(R.string.detail_no_description),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onClose()
                        onShowDetails()
                    }
                ) { Text(stringResource(R.string.wishlist_full_details)) }
                OutlinedButton(onClick = onClose) { Text(stringResource(R.string.action_close)) }
            }
        }
    }
}
