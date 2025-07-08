package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.content.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*

/**
 * ViewModel für die Screenshot-Gallery.
 * Startet die Fullscreen-Activity für die Bildergalerie.
 */
class ScreenshotGalleryViewModel : ViewModel() {

    /**
     * Öffnet die Vollbild-Galerie mit dem angegebenen Screenshot.
     */
    fun openFullscreenGallery(
        imageIndex: Int,
        screenshots: List<String>,
        context: Context,
        imageQuality: ImageQuality = ImageQuality.HIGH,
    ) {
        // Starte die Fullscreen Activity
        ScreenshotGalleryActivity.start(context, screenshots, imageIndex, imageQuality)
    }
} 