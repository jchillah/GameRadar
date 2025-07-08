package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.content.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*

/**
 * ViewModel für den Trailer-Player.
 * Startet die Fullscreen-Activity für die Video-Wiedergabe.
 */
class TrailerPlayerViewModel : ViewModel() {

    /**
     * Öffnet einen Trailer in der Fullscreen-Activity.
     */
    fun openTrailer(movie: Movie, context: Context) {
        // Starte die Fullscreen Activity
        movie.urlMax?.let { TrailerPlayerActivity.start(context, it, movie.name) }
    }
} 