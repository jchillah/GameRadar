package de.syntax_institut.androidabschlussprojekt

import android.app.*
import de.syntax_institut.androidabschlussprojekt.di.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*

/**
 * MyApplication
 * Initialisiert Koin für Dependency Injection.
 */
class GameRadarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GameRadarApp)
            modules(networkModule)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }
}