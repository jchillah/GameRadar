package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun WishlistHeader(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.wishlist_tab),
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
    )
}
