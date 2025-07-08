package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

/**
 * Modul für ViewModels.
 */
val viewModelModule = module {
    viewModel { SearchViewModel(get(), get()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { TrailerPlayerViewModel() }
    viewModel { ScreenshotGalleryViewModel() }
}

