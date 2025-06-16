package de.syntax_institut.androidabschlussprojekt.di

import org.koin.dsl.module
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.DetailViewModel
import org.koin.core.module.dsl.viewModel

/**
 * Modul für ViewModels.
 */
val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}

