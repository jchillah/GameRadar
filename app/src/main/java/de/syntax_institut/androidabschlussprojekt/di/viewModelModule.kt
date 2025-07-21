package de.syntax_institut.androidabschlussprojekt.di

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

/**
 * DI-Modul für alle ViewModels der App.
 *
 * Stellt alle ViewModels für die verschiedenen UI-Screens bereit.
 */
val viewModelModule = module {
    /**
     * ViewModel für die Suche nach Spielen.
     */
    viewModel {
        SearchViewModel(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    /**
     * ViewModel für Spieldetails.
     */
    viewModel { (savedStateHandle: SavedStateHandle) ->
        DetailViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            savedStateHandle,
            get()
        )
    }
    /**
     * ViewModel für Favoriten.
     */
    viewModel {
        FavoritesViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    /**
     * ViewModel für die Wunschliste.
     */
    viewModel {
        WishlistViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    /**
     * ViewModel für Einstellungen.
     */
    viewModel { SettingsViewModel(get()) }
    /**
     * ViewModel für Screenshot-Galerie.
     */
    viewModel { ScreenshotGalleryViewModel() }
}
