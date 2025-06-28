package de.syntax_institut.androidabschlussprojekt.di

import org.koin.dsl.module
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.DetailViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.FavoritesViewModel
import android.content.Context
import org.koin.androidx.viewmodel.dsl.viewModel

/**
 * Modul für ViewModels.
 */
val viewModelModule = module {
    viewModel { SearchViewModel(get(), get<Context>()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { FavoritesViewModel(get()) }
}

