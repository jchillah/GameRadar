package de.syntax_institut.androidabschlussprojekt.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi

/**
 * Modul für Netzwerk-Abhängigkeiten.
 */
val networkModule = module {
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }
    single { get<Retrofit>().create(RawgApi::class.java) }
}