package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*

@Composable
fun GameRatingButton(
    modifier: Modifier = Modifier,
    rating: Float,
    onRatingChanged: (Float) -> Unit = {},
) {
    var showRatingDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showRatingDialog = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = stringResource(R.string.game_rating_change),
            tint = if (rating > 0) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
        )
    }

    if (showRatingDialog) {
        RatingDialog(
            currentRating = rating,
            onRatingChanged = { newRating ->
                onRatingChanged(newRating)
                showRatingDialog = false
            },
            onDismiss = { showRatingDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameRatingButtonPreview() {
    GameRatingButton(rating = 3.5f)
}