package de.syntax_institut.androidabschlussprojekt.di

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

/** Modul für ViewModels. */
val viewModelModule = module {
    viewModel {
        SearchViewModel(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
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
    viewModel { SettingsViewModel(get()) }
    viewModel { ScreenshotGalleryViewModel() }
    viewModel { TrailerPlayerViewModel() }
}
