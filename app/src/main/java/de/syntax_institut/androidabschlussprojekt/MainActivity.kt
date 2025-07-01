package de.syntax_institut.androidabschlussprojekt

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*

/**
 * MainActivity
 * Einstiegspunkt der App.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}