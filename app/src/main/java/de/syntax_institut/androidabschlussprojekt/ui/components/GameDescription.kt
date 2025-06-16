package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameDescription(description: String?) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(description ?: "No description available.")
}
