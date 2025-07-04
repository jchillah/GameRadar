package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil3.compose.*

@Composable
fun GameHeaderImage(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentScale = ContentScale.Crop,
    )
}

@Preview(showBackground = true)
@Composable
fun GameHeaderImagePreview() {
    GameHeaderImage(
        imageUrl = "https://media.rawg.io/media/games/4fb/4fb548e4816c84d1d70f1a228fb167cc.jpg"
    )
}