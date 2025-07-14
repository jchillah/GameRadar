package de.syntax_institut.androidabschlussprojekt.di

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

/** Modul für ViewModels. */
val viewModelModule = module {
    viewModel {
        SearchViewModel(
            get(), // LoadGamesUseCase
            get(), // GetPlatformsUseCase
            get(), // GetGenresUseCase
            get(), // ClearCacheUseCase
            get() // GetCacheSizeUseCase
        )
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        DetailViewModel(
            getGameDetailUseCase = get(),
            toggleFavoriteUseCase = get(),
            isFavoriteUseCase = get(),
            toggleWishlistGameUseCase = get(),
            isInWishlistUseCase = get(),
            savedStateHandle = savedStateHandle
        )
    }
    viewModel {
        FavoritesViewModel(
            get(), // GetAllFavoritesUseCase
            get(), // ClearAllFavoritesUseCase
            get(), // RemoveFavoriteUseCase
            get(), // SyncFavoritesWithApiUseCase
            get(), // RawgApi
            get() // AppDatabase
        )
    }
    viewModel {
        WishlistViewModel(
            addWishlistGameUseCase = get(),
            removeWishlistGameUseCase = get(),
            toggleWishlistGameUseCase = get(),
            getAllWishlistGamesUseCase = get(),
            clearAllWishlistGamesUseCase = get(),
            isInWishlistUseCase = get(),
            getWishlistGameByIdUseCase = get(),
            getWishlistCountUseCase = get(),
            searchWishlistGamesUseCase = get(),
            exportWishlistToUriUseCase = get(),
            importWishlistFromUriUseCase = get()
        )
    }
    viewModel { SettingsViewModel(get()) }
    viewModel { ScreenshotGalleryViewModel() }
    viewModel { TrailerPlayerViewModel() }
}
