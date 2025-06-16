package de.syntax_institut.androidabschlussprojekt

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*

/**
 * MainActivity
 * Einstiegspunkt der App.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}