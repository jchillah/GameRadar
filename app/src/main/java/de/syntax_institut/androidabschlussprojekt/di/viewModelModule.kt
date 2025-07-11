package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

/**
 * Modul für ViewModels.
 */
val viewModelModule = module {
    viewModel {
        SearchViewModel(
            get(), // LoadGamesUseCase
            get(), // GetPlatformsUseCase
            get(), // GetGenresUseCase
            get(), // ClearCacheUseCase
            get()  // GetCacheSizeUseCase
        )
    }
    viewModel {
        DetailViewModel(
            get(), // GetGameDetailUseCase
            get(), // ToggleFavoriteUseCase
            get(), // IsFavoriteUseCase
            get()  // GetFavoriteByIdUseCase
        )
    }
    viewModel {
        FavoritesViewModel(
            get(), // GetAllFavoritesUseCase
            get(), // ClearAllFavoritesUseCase
            get(), // RemoveFavoriteUseCase
            get(), // SyncFavoritesWithApiUseCase
            get(),  // RawgApi
            get()   // AppDatabase
        )
    }
    viewModel { SettingsViewModel(get()) }
    viewModel { ScreenshotGalleryViewModel() }
    viewModel { TrailerPlayerViewModel() }
}

