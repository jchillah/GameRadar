package de.syntax_institut.androidabschlussprojekt

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*

/**
 * MainActivity
 * Einstiegspunkt der App.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(2, 1))
        setContent {
            MyAppTheme {
                App(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}