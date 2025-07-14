package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import org.koin.dsl.*

val useCaseModule = module {
    single { AddFavoriteUseCase(get()) }
    single { RemoveFavoriteUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { GetAllFavoritesUseCase(get()) }
    single { ClearAllFavoritesUseCase(get()) }
    single { IsFavoriteUseCase(get()) }
    single { GetFavoriteByIdUseCase(get()) }
    single { SearchFavoritesUseCase(get()) }
    single { SyncFavoritesWithApiUseCase(get()) }
    single { GetFavoriteCountUseCase(get()) }
    single { LoadGamesUseCase(get()) }
    single { GetGameDetailUseCase(get()) }
    single { GetPlatformsUseCase(get()) }
    single { GetGenresUseCase(get()) }
    single { ClearCacheUseCase(get()) }
    single { GetCacheSizeUseCase(get()) }

    // Wishlist UseCases
    single { AddWishlistGameUseCase(get()) }
    single { RemoveWishlistGameUseCase(get()) }
    single { ToggleWishlistGameUseCase(get()) }
    single { GetAllWishlistGamesUseCase(get()) }
    single { ClearAllWishlistGamesUseCase(get()) }
    single { IsInWishlistUseCase(get()) }
    single { GetWishlistGameByIdUseCase(get()) }
    single { SearchWishlistGamesUseCase(get()) }
    single { GetWishlistCountUseCase(get()) }
    single { ExportWishlistToUriUseCase(get()) }
    single { ImportWishlistFromUriUseCase(get()) }
}
