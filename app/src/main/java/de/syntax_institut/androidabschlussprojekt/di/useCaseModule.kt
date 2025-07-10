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
} 