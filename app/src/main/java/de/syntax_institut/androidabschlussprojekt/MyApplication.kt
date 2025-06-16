package de.syntax_institut.androidabschlussprojekt

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import de.syntax_institut.androidabschlussprojekt.di.networkModule
import de.syntax_institut.androidabschlussprojekt.di.repositoryModule
import de.syntax_institut.androidabschlussprojekt.di.viewModelModule

/**
 * MyApplication
 * Initialisiert Koin für Dependency Injection.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}