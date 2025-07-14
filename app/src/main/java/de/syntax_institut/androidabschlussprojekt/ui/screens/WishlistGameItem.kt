package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

@Composable
fun WishlistGameItem(
    game: Game,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    onToggleWishlist: (Boolean) -> Unit = {},
    isInWishlist: Boolean = true,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove, enabled = true) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.wishlist_remove)
                )
            }
            IconToggleButton(
                checked = isInWishlist,
                onCheckedChange = onToggleWishlist,
                enabled = true
            ) {
                Icon(
                    imageVector =
                        if (isInWishlist) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription =
                        if (isInWishlist) stringResource(R.string.wishlist_marked)
                        else stringResource(R.string.wishlist_not_marked)
                )
            }
        }
    }
}
