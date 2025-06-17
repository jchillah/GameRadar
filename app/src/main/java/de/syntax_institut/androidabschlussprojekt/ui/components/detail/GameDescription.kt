package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GameDescription(description: String?) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(description ?: "No description available.", style = MaterialTheme.typography.bodyLarge)
}

@Preview(showBackground = true)
@Composable
fun GameDescriptionPreview() {
    GameDescription(description = "This is a sample game description.")
}